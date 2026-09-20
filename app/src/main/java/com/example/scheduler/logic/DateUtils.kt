package com.example.scheduler.logic

import java.text.SimpleDateFormat
import java.util.*

/**
 * Formats a millisecond timestamp into a human-readable "MMM d" string.
 * @param millis The timestamp to format.
 * @param useUtc If true, uses UTC timezone (required for DatePicker values).
 */
fun formatDate(millis: Long?, useUtc: Boolean = false): String {
    if (millis == null) return ""
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    if (useUtc) formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

/**
 * Formats a date range into a "MMM d - MMM d" string.
 * @param useUtc If true, uses UTC timezone (required for DatePicker values).
 */
fun formatDateRange(start: Long?, end: Long?, useUtc: Boolean = false): String {
    if (start == null || end == null) return ""
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    if (useUtc) formatter.timeZone = TimeZone.getTimeZone("UTC")
    return "${formatter.format(Date(start))} - ${formatter.format(Date(end))}"
}

/**
 * Formats a millisecond timestamp into a "MMMM yyyy" string.
 */
fun formatMonthYear(millis: Long?): String {
    if (millis == null) return "Unknown"
    val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

/**
 * Returns a time-appropriate greeting based on the current hour.
 */
fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}
