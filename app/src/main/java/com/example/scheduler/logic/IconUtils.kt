package com.example.scheduler.logic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.scheduler.data.model.Event

/**
 * Maps the stored icon name to a Material Design ImageVector.
 */
val Event.icon: ImageVector
    get() = getIconForName(iconName)

/**
 * Provides the ImageVector associated with a specific icon name string.
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

/**
 * Returns the string name associated with a specific Material ImageVector.
 */
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
