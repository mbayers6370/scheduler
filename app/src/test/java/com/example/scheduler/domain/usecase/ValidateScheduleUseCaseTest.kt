package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidateScheduleUseCaseTest {
    private val validateScheduleUseCase = ValidateScheduleUseCase()
    private val hour = 60 * 60 * 1000L
    private val userId = "test_user"

    @Test
    fun `partial overlap at start should conflict`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 9 * hour + 30 * 60 * 1000L, durationMinutes = 60)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertEquals("1", result?.id)
    }

    @Test
    fun `new event inside existing should conflict`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 10 * hour, durationMinutes = 120)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 11 * hour, durationMinutes = 30)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertEquals("1", result?.id)
    }

    @Test
    fun `existing inside new event should conflict`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 11 * hour, durationMinutes = 30)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 10 * hour, durationMinutes = 120)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertEquals("1", result?.id)
    }

    @Test
    fun `identical time ranges should conflict`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertEquals("1", result?.id)
    }

    @Test
    fun `back to back events should NOT conflict`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 11 * hour, durationMinutes = 60)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertNull(result)
    }

    @Test
    fun `clearly separated events should NOT conflict`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 12 * hour, durationMinutes = 60)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertNull(result)
    }

    @Test
    fun `collections should be ignored in conflict detection`() {
        val existing = Event("1", userId, "Collection", "", "", "", isCollection = true, timestamp = 10 * hour, durationMinutes = 60)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertNull(result)
    }

    @Test
    fun `editing the same event ID should NOT conflict with itself`() {
        val existing = Event("1", userId, "Existing", "", "", "", timestamp = 10 * hour, durationMinutes = 60)
        val editEvent = Event("1", userId, "Updated", "", "", "", timestamp = 10 * hour + 15 * 60 * 1000L, durationMinutes = 30)
        
        val result = validateScheduleUseCase(editEvent, listOf(existing))
        assertNull(result)
    }

    @Test
    fun `events without timestamps should be ignored`() {
        val existing = Event("1", userId, "No Time", "", "", "", timestamp = null)
        val newEvent = Event("2", userId, "New", "", "", "", timestamp = 10 * hour)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertNull(result)
    }

    @Test
    fun `overlap at the very end millisecond should conflict`() {
        val start1 = 1000L
        val dur1 = 60L // 60 mins
        val end1 = start1 + dur1 * 60 * 1000L // 3601000
        
        val existing = Event("1", userId, "A", "", "", "", timestamp = start1, durationMinutes = dur1.toInt())
        val newEvent = Event("2", userId, "B", "", "", "", timestamp = end1 - 1, durationMinutes = 30)
        
        val result = validateScheduleUseCase(newEvent, listOf(existing))
        assertEquals("1", result?.id)
    }
}
