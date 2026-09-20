package com.example.scheduler.domain.usecase

import com.example.scheduler.data.model.Event

/**
 * Use Case for identifying scheduling conflicts.
 * Encapsulates the overlap detection algorithm.
 */
class ValidateScheduleUseCase {

    /**
     * Scans a list of events to find a conflict with the provided item.
     * Identifies overlaps based on exact time ranges.
     * @return The conflicting event if found, null otherwise.
     */
    operator fun invoke(newEvent: Event, allEvents: List<Event>): Event? {
        // Validation ignores collections and events without valid start times
        if (newEvent.isCollection || newEvent.timestamp == null) return null

        val start1 = newEvent.timestamp
        val end1 = start1 + (newEvent.durationMinutes * 60 * 1000L)

        return allEvents.find { existing ->
            // Skip self, collections, and items without timestamps
            if (existing.id == newEvent.id || existing.isCollection || existing.timestamp == null) {
                return@find false
            }
            
            val start2 = existing.timestamp
            val end2 = start2 + (existing.durationMinutes * 60 * 1000L)
            
            // True Overlap Algorithm: (StartA < EndB) AND (StartB < EndA)
            // This logic correctly identifies any intersection while allowing back-to-back events.
            start1 < end2 && start2 < end1
        }
    }
}
