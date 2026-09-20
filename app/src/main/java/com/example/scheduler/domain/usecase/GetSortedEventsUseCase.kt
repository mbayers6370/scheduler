package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.Event
import java.util.Calendar

/**
 * Use Case for filtering and sorting events based on a time horizon.
 */
class GetSortedEventsUseCase {

    operator fun invoke(
        events: List<Event>,
        filter: String,
        startOfToday: Long
    ): List<Event> {
        val calendar = Calendar.getInstance().apply { timeInMillis = startOfToday }
        val endOfToday = startOfToday + 24 * 60 * 60 * 1000L
        
        val startOfNextSun = (calendar.clone() as Calendar).apply { 
            add(Calendar.DAY_OF_YEAR, 7 - (get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY)) 
        }.timeInMillis
        
        val startOfNextMonth = (calendar.clone() as Calendar).apply { 
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1) 
        }.timeInMillis

        return when (filter) {
            "Today" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < endOfToday }
            "This Week" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextSun }
            "This Month" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextMonth }
            "Collections" -> events.filter { it.isCollection }
            else -> events
        }
    }
}
