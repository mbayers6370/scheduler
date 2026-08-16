package com.example.scheduler.ui.screens

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduler.logic.PasswordHasher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Manages the application's business logic and state.
 * Implements the ViewModel component of the MVVM pattern, serving as a bridge 
 * between the Room database and the UI layer.
 */
class EventViewModel(application: Application) : AndroidViewModel(application) {
    // Repository-level access via Room DAOs
    private val db = AppDatabase.getDatabase(application)
    private val eventDao = db.eventDao()
    private val userDao = db.userDao()
    
    /**
     * Observable state for the currently authenticated user.
     */
    val currentUser = MutableStateFlow<User?>(null)

    /**
     * User preference for enabling or disabling automated alerts.
     */
    val smsAlertsEnabled = MutableStateFlow(false)

    /**
     * Continuous stream of all scheduled items retrieved from the database.
     */
    val allEvents: StateFlow<List<Event>> = eventDao.getAllEvents()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Hydrate the database with mock data if it is currently empty
        viewModelScope.launch {
            allEvents.collect { list ->
                if (list.isEmpty()) {
                    seedDatabase()
                }
            }
        }
        
        // Start the background process for monitoring upcoming event windows
        monitorUpcomingEvents()
    }

    /**
     * Background routine that monitors the user's schedule for upcoming events.
     * Triggers a simulated notification if an event is starting within 30 minutes.
     */
    private fun monitorUpcomingEvents() {
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                // Define the proximity alert window
                val threshold = now + (30 * 60 * 1000)
                
                // Verify both user preference and system-level permission before alerting
                val hasPermission = ContextCompat.checkSelfPermission(
                    getApplication(), 
                    Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED

                if (smsAlertsEnabled.value && hasPermission) {
                    allEvents.value.forEach { event ->
                        // Check if event is within the alert window and hasn't passed
                        if (event.timestamp != null && event.timestamp in now..threshold) {
                            Log.i("SmsAlert", "AUTOMATED ALERT: '${event.title}' begins in less than 30 minutes.")
                        }
                    }
                }
                
                // Delay execution to prevent unnecessary CPU usage
                kotlinx.coroutines.delay(60000)
            }
        }
    }

    // --- Authentication Logic ---

    /**
     * Validates credentials against the database.
     * Uses PasswordHasher to securely verify the salted hash.
     * @return True if successful, false otherwise.
     */
    suspend fun loginUser(username: String, password: String): Boolean {
        val user = userDao.getUser(username)
        return if (user != null && PasswordHasher.checkPassword(password, user.password)) {
            currentUser.value = user
            true
        } else {
            false
        }
    }

    /**
     * Registers a new user account.
     * Hashes and salts the password before persisting it to the database.
     * @return True if successful, false if username already exists.
     */
    suspend fun registerUser(
        username: String, 
        password: String,
        firstName: String,
        lastName: String,
        email: String
    ): Boolean {
        return try {
            val hashedPassword = PasswordHasher.hashPassword(password)
            val newUser = User(username, hashedPassword, firstName, lastName, email)
            userDao.registerUser(newUser)
            currentUser.value = newUser
            true
        } catch (e: Exception) {
            false // Username likely already taken
        }
    }

    fun logout() {
        currentUser.value = null
    }

    // --- Data Seeding ---

    private suspend fun seedDatabase() {
        val calendar = Calendar.getInstance()
        
        // Helper to get specific timestamp for Oct 2026
        fun getOct2026(day: Int, hour: Int, minute: Int = 0): Long {
            return calendar.apply {
                set(2026, Calendar.OCTOBER, day, hour, minute, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        // Helper for Nov 2026
        fun getNov2026(day: Int, hour: Int, minute: Int = 0): Long {
            return calendar.apply {
                set(2026, Calendar.NOVEMBER, day, hour, minute, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

        val mockEvents = listOf(
            Event("1", "Birthday", "October 12", "6:00 PM", "Cake", timestamp = getOct2026(12, 18)),
            Event("2", "Team Meeting", "October 14", "12:00 PM", "Groups", "Conference Room", "Bring design concepts!", "15 minutes before", timestamp = getOct2026(14, 12)),
            Event("3", "Japan Trip", "October 20 - 31", "", "Folder", isCollection = true, dateRange = "October 20 - 31", eventCount = 8, timestamp = getOct2026(20, 0)),
            Event("4", "Dentist Appointment", "November 9", "9:00 AM", "CalendarToday", timestamp = getNov2026(9, 9)),
            
            // Restored Japan Trip Sub-Events (Strictly Chronological within the collection)
            Event("J1", "Flight to Tokyo", "October 20", "10:30 AM", "Flight", "LAX Terminal 4", "Confirmation: JAL123", "2 hours before", parentCollectionId = "3", timestamp = getOct2026(20, 10, 30)),
            Event("J2", "Hotel Check-in", "October 21", "3:00 PM", "Hotel", "Park Hyatt Tokyo", "Shinjuku district", "1 hour before", parentCollectionId = "3", timestamp = getOct2026(21, 15)),
            Event("J3", "Shibuya Crossing", "October 22", "11:00 AM", "CameraAlt", "Shibuya", "The famous scramble!", "None", parentCollectionId = "3", timestamp = getOct2026(22, 11)),
            Event("J4", "Sushi Dinner", "October 23", "7:00 PM", "Restaurant", "Sukiyabashi Jiro", "Reservation confirmed", "30 minutes before", parentCollectionId = "3", timestamp = getOct2026(23, 19)),
            Event("J5", "Bullet Train to Kyoto", "October 25", "9:00 AM", "Train", "Tokyo Station", "Platform 14", "1 hour before", parentCollectionId = "3", timestamp = getOct2026(25, 9)),
            Event("J6", "Kinkaku-ji Temple", "October 26", "2:00 PM", "PhotoCamera", "Kyoto", "The Golden Pavilion", "None", parentCollectionId = "3", timestamp = getOct2026(26, 14)),
            Event("J7", "Nara Deer Park", "October 28", "10:00 AM", "NaturePeople", "Nara", "Feed the deer!", "None", parentCollectionId = "3", timestamp = getOct2026(28, 10)),
            Event("J8", "Return Flight", "October 31", "6:00 PM", "FlightTakeoff", "Narita Airport", "Terminal 2", "3 hours before", parentCollectionId = "3", timestamp = getOct2026(31, 18))
        )
        mockEvents.forEach { eventDao.insertEvent(it) }
    }

    // --- Filtering Logic ---

    private fun getStartOfToday(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    val activeEvents: StateFlow<List<Event>> = allEvents.map { events ->
        val startOfToday = getStartOfToday()
        events.map { event ->
            if (event.isCollection) {
                val count = events.count { it.parentCollectionId == event.id }
                event.copy(eventCount = count)
            } else {
                event
            }
        }.filter { it.timestamp == null || it.timestamp >= startOfToday }
            .sortedBy { it.timestamp ?: Long.MAX_VALUE }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val archivedEvents: StateFlow<List<Event>> = allEvents.map { events ->
        val startOfToday = getStartOfToday()
        events.map { event ->
            if (event.isCollection) {
                val count = events.count { it.parentCollectionId == event.id }
                event.copy(eventCount = count)
            } else {
                event
            }
        }.filter { it.timestamp != null && it.timestamp < startOfToday }
            .sortedByDescending { it.timestamp }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- CRUD Operations ---

    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventDao.insertEvent(event)
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventDao.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventDao.deleteEvent(event)
        }
    }
}
