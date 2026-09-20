package com.example.scheduler.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduler.data.database.AppDatabase
import com.example.scheduler.data.model.Event
import com.example.scheduler.data.repository.EventRepository
import com.example.scheduler.domain.usecase.GetSortedEventsUseCase
import com.example.scheduler.domain.usecase.ValidateScheduleUseCase
import com.example.scheduler.logic.SmsAlertManager
import com.example.scheduler.ui.state.MainUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * ViewModel responsible for managing scheduled events and collections.
 */
class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val eventRepository: EventRepository
    private val smsAlertManager: SmsAlertManager
    private val getSortedEventsUseCase = GetSortedEventsUseCase()
    private val validateScheduleUseCase = ValidateScheduleUseCase()

    private val _userId = MutableStateFlow<String?>(null)
    
    val smsAlertsEnabled = MutableStateFlow(false)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val allEvents: StateFlow<List<Event>> = _userId
        .flatMapLatest { id -> 
            if (id == null) flowOf(emptyList()) 
            else eventRepository.getAllEvents(id) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedFilter = MutableStateFlow("All Events")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        eventRepository = EventRepository(db.eventDao())
        
        smsAlertManager = SmsAlertManager(application, viewModelScope, allEvents, smsAlertsEnabled)
        smsAlertManager.startMonitoring()
    }

    /**
     * Sets the active user context. 
     * Passing null (e.g., during logout) clears the event stream for security.
     */
    fun setUserId(id: String?) {
        if (_userId.value == id) return
        _userId.value = id
        
        if (id != null) {
            // Hydrate the database for this specific user if empty
            viewModelScope.launch {
                eventRepository.getAllEvents(id).first().let { list ->
                    if (list.isEmpty()) seedDatabase(id)
                }
            }
        }
    }

    /**
     * Unified UI State for the Main Screen.
     */
    fun getMainUiState(userName: String): StateFlow<MainUiState> {
        return combine(allEvents, _selectedFilter) { events, filter ->
            val startOfToday = getStartOfToday()
            val filtered = getSortedEventsUseCase(events, filter, startOfToday)
                .map { if (it.isCollection) it.copy(eventCount = events.count { e -> e.parentCollectionId == it.id }) else it }
                .sortedBy { it.timestamp ?: Long.MAX_VALUE }
            
            MainUiState.Success(filtered, userName, filter)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState.Loading)
    }

    /**
     * State for the Archive screen.
     */
    val archivedEvents: StateFlow<List<Event>> = allEvents.map { events ->
        val startOfToday = getStartOfToday()
        events.filter { it.timestamp != null && it.timestamp < startOfToday }
            .sortedByDescending { it.timestamp }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) { _selectedFilter.value = filter }

    /**
     * Algorithmic validation for event scheduling.
     * Enforces ownership by stamping the event with the current userId.
     * @return The conflicting event if validation fails, null if successful.
     */
    fun validateAndSaveEvent(event: Event): Event? {
        val currentUserId = _userId.value ?: return null
        // Ensure ownership is strictly enforced at the logic layer
        val eventWithOwner = event.copy(userId = currentUserId)
        
        val conflict = validateScheduleUseCase(eventWithOwner, allEvents.value)
        if (conflict == null) {
            viewModelScope.launch { eventRepository.insertEvent(eventWithOwner) }
        }
        return conflict
    }

    /**
     * Persists a new event, ensuring it belongs to the authenticated user.
     */
    fun addEvent(event: Event) {
        val currentUserId = _userId.value ?: return
        viewModelScope.launch { 
            eventRepository.insertEvent(event.copy(userId = currentUserId)) 
        }
    }

    fun updateEvent(event: Event) {
        val currentUserId = _userId.value ?: return
        viewModelScope.launch { 
            eventRepository.updateEvent(event.copy(userId = currentUserId)) 
        }
    }

    fun deleteEvent(event: Event) {
        val currentUserId = _userId.value ?: return
        // Ensure the user owns the record they are attempting to delete
        if (event.userId == currentUserId) {
            viewModelScope.launch { eventRepository.deleteEvent(event) }
        }
    }

    private fun getStartOfToday() = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis

    private suspend fun seedDatabase(userId: String) {
        val cal = Calendar.getInstance()
        fun getT(d: Int, h: Int) = cal.apply { set(2026, Calendar.OCTOBER, d, h, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val mock = listOf(
            Event("1", userId, "Birthday", "Oct 12", "6 PM", "Cake", timestamp = getT(12, 18), durationMinutes = 240),
            Event("2", userId, "Meeting", "Oct 14", "12 PM", "Groups", timestamp = getT(14, 12), durationMinutes = 60),
            Event("3", userId, "Japan", "Oct 20-31", "", "Folder", isCollection = true, timestamp = getT(20, 0)),
            Event("J1", userId, "Flight", "Oct 20", "10 AM", "Flight", parentCollectionId = "3", timestamp = getT(20, 10), durationMinutes = 720)
        )
        mock.forEach { eventRepository.insertEvent(it) }
    }
}
