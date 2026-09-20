package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidateScheduleUseCaseTest {
    private val validateScheduleUseCase = ValidateScheduleUseCase()

    @Test
    fun `when events do not overlap, returns null`() {
        val hour = 60 * 60 * 1000L
        val existingEvent = Event(id = "1", userId = "u1", title = "A", date = "", time = "", iconName = "", timestamp = 10 * hour, durationMinutes = 30)
        val newEvent = Event(id = "2", userId = "u1", title = "B", date = "", time = "", iconName = "", timestamp = 11 * hour, durationMinutes = 30)
        
        val result = validateScheduleUseCase(newEvent, listOf(existingEvent))
        
        assertNull(result)
    }

    @Test
    fun `when events overlap, returns the conflicting event`() {
        val hour = 60 * 60 * 1000L
        val existingEvent = Event(id = "1", userId = "u1", title = "A", date = "", time = "", iconName = "", timestamp = 10 * hour, durationMinutes = 60)
        val newEvent = Event(id = "2", userId = "u1", title = "B", date = "", time = "", iconName = "", timestamp = 10 * hour + 15 * 60 * 1000L, durationMinutes = 30)
        
        val result = validateScheduleUseCase(newEvent, listOf(existingEvent))
        
        assertEquals(existingEvent.id, result?.id)
    }

    @Test
    fun `when events are back-to-back, returns null`() {
        val existingEvent = Event(id = "1", userId = "u1", title = "A", date = "", time = "", iconName = "", timestamp = 1000, durationMinutes = 60)
        val newEvent = Event(id = "2", userId = "u1", title = "B", date = "", time = "", iconName = "", timestamp = 1060, durationMinutes = 30) 
        // 1000 + 60*1000 = 61000ms. Wait, timestamp is in ms. 60 mins = 3600000ms.
        
        val start1 = 1000000L
        val end1 = start1 + (60 * 60 * 1000L) // 4600000
        
        val existing = existingEvent.copy(timestamp = start1, durationMinutes = 60)
        val newEvt = newEvent.copy(timestamp = end1, durationMinutes = 30)
        
        val result = validateScheduleUseCase(newEvt, listOf(existing))
        
        assertNull(result)
    }
}
