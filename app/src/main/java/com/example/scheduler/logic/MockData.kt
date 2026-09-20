package com.example.scheduler.logic

import com.example.scheduler.data.model.Event

/**
 * Provides static mock data for legacy collection views.
 */
fun getMockEventsForCollection(title: String): List<Event> {
    if (title == "Japan Trip") {
        return listOf(
            Event("J1", "Flight to Tokyo", "October 20", "10:30 AM", "Flight", "LAX Terminal 4", "Confirmation: JAL123", "2 hours before"),
            Event("J2", "Hotel Check-in", "October 21", "3:00 PM", "Hotel", "Park Hyatt Tokyo", "Shinjuku district", "1 hour before"),
            Event("J3", "Shibuya Crossing", "October 22", "11:00 AM", "CameraAlt", "Shibuya", "The famous scramble!", "None"),
            Event("J4", "Sushi Dinner", "October 23", "7:00 PM", "Restaurant", "Sukiyabashi Jiro", "Reservation confirmed", "30 minutes before"),
            Event("J5", "Bullet Train to Kyoto", "October 25", "9:00 AM", "Train", "Tokyo Station", "Platform 14", "1 hour before"),
            Event("J6", "Kinkaku-ji Temple", "October 26", "2:00 PM", "PhotoCamera", "Kyoto", "The Golden Pavilion", "None"),
            Event("J7", "Nara Deer Park", "October 28", "10:00 AM", "NaturePeople", "Nara", "Feed the deer!", "None"),
            Event("J8", "Return Flight", "October 31", "6:00 PM", "FlightTakeoff", "Narita Airport", "Terminal 2", "3 hours before")
        )
    }
    
    val count = when {
        title.contains("September") -> 12
        title.contains("August") -> 24
        title.contains("July") -> 18
        title.contains("June") -> 31
        title.contains("May") -> 22
        else -> 5
    }
    
    return List(count) { i ->
        Event(
            id = "A$i",
            userId = "legacy_user",
            title = "Archived Event ${i + 1}",
            date = "Past Date",
            time = "${9 + (i % 8)}:00 AM",
            iconName = "Event",
            location = "Old Location",
            notes = "Notes for archived event ${i + 1}",
            reminder = "None"
        )
    }
}
