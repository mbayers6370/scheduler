package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import java.text.SimpleDateFormat
import java.util.*

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Task IV: Industry Best Practices
 * 
 * Common UI components and utility functions used throughout the application.
 * Ensures consistent theme, readable naming, and concise code structure.
 */

val Event.icon: ImageVector
    get() = getIconForName(iconName)

/**
 * Maps icon names to their respective ImageVectors.
 * Used for persistent storage of icons in the SQLite database (Task II).
 */
fun getIconForName(name: String): ImageVector {
    return when (name) {
        "Cake" -> Icons.Default.Cake
        "Groups" -> Icons.Default.Groups
        "CalendarToday" -> Icons.Default.CalendarToday
        "Flight" -> Icons.Default.Flight
        "Restaurant" -> Icons.Default.Restaurant
        "School" -> Icons.Default.School
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "Work" -> Icons.Default.Work
        "Celebration" -> Icons.Default.Celebration
        "LocalBar" -> Icons.Default.LocalBar
        "SportsEsports" -> Icons.Default.SportsEsports
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "Brush" -> Icons.Default.Brush
        "MusicNote" -> Icons.Default.MusicNote
        "Hotel" -> Icons.Default.Hotel
        "CameraAlt" -> Icons.Default.CameraAlt
        "Train" -> Icons.Default.Train
        "PhotoCamera" -> Icons.Default.PhotoCamera
        "NaturePeople" -> Icons.Default.NaturePeople
        "FlightTakeoff" -> Icons.Default.FlightTakeoff
        "Folder" -> Icons.Default.Folder
        "Person" -> Icons.Default.Person
        else -> Icons.Default.Event
    }
}

fun getNameForIcon(icon: ImageVector): String {
    return when (icon) {
        Icons.Default.Cake -> "Cake"
        Icons.Default.Groups -> "Groups"
        Icons.Default.CalendarToday -> "CalendarToday"
        Icons.Default.Flight -> "Flight"
        Icons.Default.Restaurant -> "Restaurant"
        Icons.Default.School -> "School"
        Icons.Default.FitnessCenter -> "FitnessCenter"
        Icons.Default.Work -> "Work"
        Icons.Default.Celebration -> "Celebration"
        Icons.Default.LocalBar -> "LocalBar"
        Icons.Default.SportsEsports -> "SportsEsports"
        Icons.Default.ShoppingCart -> "ShoppingCart"
        Icons.Default.Brush -> "Brush"
        Icons.Default.MusicNote -> "MusicNote"
        Icons.Default.Hotel -> "Hotel"
        Icons.Default.CameraAlt -> "CameraAlt"
        Icons.Default.Train -> "Train"
        Icons.Default.PhotoCamera -> "PhotoCamera"
        Icons.Default.NaturePeople -> "NaturePeople"
        Icons.Default.FlightTakeoff -> "FlightTakeoff"
        Icons.Default.Folder -> "Folder"
        Icons.Default.Person -> "Person"
        else -> "Event"
    }
}

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

fun formatDate(millis: Long?): String {
    if (millis == null) return ""
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun formatDateRange(start: Long?, end: Long?): String {
    if (start == null || end == null) return ""
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return "${formatter.format(Date(start))} - ${formatter.format(Date(end))}"
}

fun formatMonthYear(millis: Long?): String {
    if (millis == null) return "Unknown"
    val formatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

@Composable
fun HeaderSection(
    name: String,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${getGreeting()}, $name",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Light,
            fontSize = 28.sp,
            color = Color.White
        )
        
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(1.dp, Color.White, CircleShape)
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 * Standard text field component for consistent UI across the app.
 * Adheres to Project Three naming and styling standards.
 */
@Composable
fun StandardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = PoppinsFamily
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        minLines = minLines,
        readOnly = readOnly,
        enabled = onClick == null, // Disable manual typing if it's meant to be clicked
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.5f),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledBorderColor = Color.White.copy(alpha = 0.3f),
            disabledLabelColor = Color.White.copy(alpha = 0.5f),
            disabledTextColor = Color.White
        )
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = Color.White, 
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                fontFamily = PoppinsFamily,
                fontSize = 18.sp,
                color = Color.White
            )
        }
        
        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}
