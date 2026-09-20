package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.Event

/**
 * Use Case for identifying scheduling conflicts.
 * Encapsulates the overlap detection algorithm.
 */
class ValidateScheduleUseCase {

    companion object {
        private const val BUFFER_MS = 30 * 60 * 1000L // 30 minute margin
    }

    /**
     * Scans a list of events to find a conflict with the provided item.
     * Includes a 30-minute buffer on either side of events.
     * @return The conflicting event if found, null otherwise.
     */
    operator fun invoke(newEvent: Event, allEvents: List<Event>): Event? {
        if (newEvent.isCollection || newEvent.timestamp == null) return null

        val start1 = newEvent.timestamp
        val end1 = start1 + (newEvent.durationMinutes * 60 * 1000L)

        return allEvents.find { existing ->
            if (existing.isCollection || existing.timestamp == null || existing.id == newEvent.id) return@find false
            
            val start2 = existing.timestamp
            val end2 = start2 + (existing.durationMinutes * 60 * 1000L)
            
            // Conflict exists if the time ranges overlap OR fall within the 30m buffer
            // Intersection Logic: (StartA < EndB + Buffer) AND (StartB < EndA + Buffer)
            start1 < (end2 + BUFFER_MS) && start2 < (end1 + BUFFER_MS)
        }
    }
}
