package com.example.scheduler.logic

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.scheduler.data.model.Event

/**
 * Maps the stored icon name to an outlined Material Design ImageVector.
 */
val Event.icon: ImageVector
    get() = getIconForName(iconName)

/**
 * Provides the outlined ImageVector associated with a specific icon name string.
 */
fun getIconForName(name: String): ImageVector {
    return when (name) {
        "Cake" -> Icons.Outlined.Cake
        "Groups" -> Icons.Outlined.Groups
        "CalendarToday" -> Icons.Outlined.CalendarToday
        "Flight" -> Icons.Outlined.Flight
        "Restaurant" -> Icons.Outlined.Restaurant
        "School" -> Icons.Outlined.School
        "FitnessCenter" -> Icons.Outlined.FitnessCenter
        "Work" -> Icons.Outlined.Work
        "Celebration" -> Icons.Outlined.Celebration
        "LocalBar" -> Icons.Outlined.LocalBar
        "SportsEsports" -> Icons.Outlined.SportsEsports
        "ShoppingCart" -> Icons.Outlined.ShoppingCart
        "Brush" -> Icons.Outlined.Brush
        "MusicNote" -> Icons.Outlined.MusicNote
        "Hotel" -> Icons.Outlined.Hotel
        "CameraAlt" -> Icons.Outlined.CameraAlt
        "Train" -> Icons.Outlined.Train
        "PhotoCamera" -> Icons.Outlined.PhotoCamera
        "NaturePeople" -> Icons.Outlined.NaturePeople
        "FlightTakeoff" -> Icons.Outlined.FlightTakeoff
        "Folder" -> Icons.Outlined.Folder
        "Person" -> Icons.Outlined.Person
        else -> Icons.Outlined.Event
    }
}

/**
 * Returns the string name associated with a specific Material ImageVector.
 */
fun getNameForIcon(icon: ImageVector): String {
    return when (icon) {
        Icons.Outlined.Cake -> "Cake"
        Icons.Outlined.Groups -> "Groups"
        Icons.Outlined.CalendarToday -> "CalendarToday"
        Icons.Outlined.Flight -> "Flight"
        Icons.Outlined.Restaurant -> "Restaurant"
        Icons.Outlined.School -> "School"
        Icons.Outlined.FitnessCenter -> "FitnessCenter"
        Icons.Outlined.Work -> "Work"
        Icons.Outlined.Celebration -> "Celebration"
        Icons.Outlined.LocalBar -> "LocalBar"
        Icons.Outlined.SportsEsports -> "SportsEsports"
        Icons.Outlined.ShoppingCart -> "ShoppingCart"
        Icons.Outlined.Brush -> "Brush"
        Icons.Outlined.MusicNote -> "MusicNote"
        Icons.Outlined.Hotel -> "Hotel"
        Icons.Outlined.CameraAlt -> "CameraAlt"
        Icons.Outlined.Train -> "Train"
        Icons.Outlined.PhotoCamera -> "PhotoCamera"
        Icons.Outlined.NaturePeople -> "NaturePeople"
        Icons.Outlined.FlightTakeoff -> "FlightTakeoff"
        Icons.Outlined.Folder -> "Folder"
        Icons.Outlined.Person -> "Person"
        else -> "Event"
    }
}
