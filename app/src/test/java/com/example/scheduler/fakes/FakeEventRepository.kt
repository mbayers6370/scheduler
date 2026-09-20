package com.example.scheduler.fakes

import com.example.scheduler.data.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Memory-based repository for high-speed architectural testing.
 */
class FakeEventRepository {
    private val _events = MutableStateFlow<List<Event>>(emptyList())

    fun getAllEvents(userId: String): Flow<List<Event>> = _events.asStateFlow().map { list ->
        list.filter { it.userId == userId }
    }

    suspend fun insertEvent(event: Event) {
        val current = _events.value.toMutableList()
        current.removeAll { it.id == event.id }
        current.add(event)
        _events.value = current
    }

    suspend fun updateEvent(event: Event) = insertEvent(event)

    suspend fun deleteEvent(event: Event) {
        val current = _events.value.toMutableList()
        current.removeAll { it.id == event.id }
        _events.value = current
    }
}
