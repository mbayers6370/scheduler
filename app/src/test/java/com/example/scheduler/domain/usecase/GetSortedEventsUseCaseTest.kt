package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.Event
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class GetSortedEventsUseCaseTest {
    private val useCase = GetSortedEventsUseCase()

    @Test
    fun `filter Today returns only today's events`() {
        val today = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 10, 10, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val startOfToday = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 10, 0, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        
        val eventToday = Event(id = "1", userId = "u1", title = "Today", date = "", time = "", iconName = "", timestamp = today)
        val eventTomorrow = Event(id = "2", userId = "u1", title = "Tomorrow", date = "", time = "", iconName = "", timestamp = today + 24 * 60 * 60 * 1000L)
        
        val result = useCase(listOf(eventToday, eventTomorrow), "Today", startOfToday)
        
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
    }
}
