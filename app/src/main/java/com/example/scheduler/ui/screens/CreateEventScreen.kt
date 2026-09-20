package com.example.scheduler.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.logic.formatDate
import com.example.scheduler.logic.getNameForIcon
import com.example.scheduler.logic.icon
import com.example.scheduler.ui.components.StandardTextField
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Screen for creating or editing individual events.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    event: Event? = null,
    parentCollectionId: String? = null,
    onBackClick: () -> Unit,
    onCreateClick: (Event) -> Unit,
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var notes by remember { mutableStateOf(event?.notes ?: "") }
    var selectedIcon by remember { mutableStateOf(event?.icon ?: Icons.Default.Event) }
    var durationMinutes by remember { mutableStateOf(event?.durationMinutes ?: 60) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDurationPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                return utcTimeMillis >= today
            }
        }
    )
    val selectedDateText = if (datePickerState.selectedDateMillis != null) formatDate(datePickerState.selectedDateMillis, useUtc = true) else (event?.date ?: "")
    var selectedTime by remember { mutableStateOf(event?.time ?: "12:00 PM") }
    val timeOptions = remember { (0..23).flatMap { h -> listOf(0, 30).map { m ->
        val ampm = if (h < 12) "AM" else "PM"; val displayH = when { h == 0 -> 12; h > 12 -> h - 12; else -> h }
        String.format(java.util.Locale.getDefault(), "%d:%02d %s", displayH, m, ampm)
    } } }
    val iconOptions = listOf(Icons.Default.Event, Icons.Default.Cake, Icons.Default.Groups, Icons.Default.CalendarToday, Icons.Default.Flight, Icons.Default.Restaurant, Icons.Default.School, Icons.Default.FitnessCenter, Icons.Default.Work, Icons.Default.Celebration, Icons.Default.LocalBar, Icons.Default.SportsEsports, Icons.Default.ShoppingCart, Icons.Default.Brush, Icons.Default.MusicNote)

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
        }
        Text(text = if (event != null) "Edit Event" else "New Event", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        StandardTextField(value = title, onValueChange = { title = it }, label = "Event Title")
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { StandardTextField(value = selectedDateText, onValueChange = {}, label = "Date", readOnly = true, onClick = { showDatePicker = true }) }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(expanded = showTimePicker, onExpandedChange = { showTimePicker = !showTimePicker }) {
                    StandardTextField(
                        value = selectedTime, 
                        onValueChange = {}, 
                        label = "Time", 
                        readOnly = true, 
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = showTimePicker, 
                        onDismissRequest = { showTimePicker = false }
                    ) {
                        timeOptions.forEach { opt -> DropdownMenuItem(text = { Text(opt, fontFamily = PoppinsFamily) }, onClick = { selectedTime = opt; showTimePicker = false }) }
                    }
                }
            }
        }
        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK", color = Color.White) } },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color.Gray) } },
                colors = DatePickerDefaults.colors(containerColor = PrimaryDark, titleContentColor = Color.White, headlineContentColor = Color.White, selectedYearContainerColor = Color.White, selectedDayContainerColor = Color.White, selectedDayContentColor = Color.Black)
            ) { DatePicker(state = datePickerState, showModeToggle = false, colors = DatePickerDefaults.colors(containerColor = PrimaryDark, titleContentColor = Color.White, headlineContentColor = Color.White, selectedDayContainerColor = Color.White, selectedDayContentColor = Color.Black)) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = showDurationPicker,
                    onExpandedChange = { showDurationPicker = !showDurationPicker }
                ) {
                    StandardTextField(
                        value = when(durationMinutes) {
                            30 -> "30 Minutes"
                            60 -> "1 Hour"
                            120 -> "2 Hours"
                            240 -> "4 Hours"
                            else -> "$durationMinutes Mins"
                        },
                        onValueChange = {},
                        label = "Duration",
                        readOnly = true,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showDurationPicker,
                        onDismissRequest = { showDurationPicker = false }
                    ) {
                        listOf(30, 60, 120, 240).forEach { mins ->
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        when(mins) {
                                            30 -> "30 Minutes"
                                            60 -> "1 Hour"
                                            120 -> "2 Hours"
                                            240 -> "4 Hours"
                                            else -> "$mins Mins"
                                        },
                                        fontFamily = PoppinsFamily
                                    ) 
                                },
                                onClick = {
                                    durationMinutes = mins
                                    showDurationPicker = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                // Keep layout balanced
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = location, onValueChange = { location = it }, label = "Location")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = notes, onValueChange = { notes = it }, label = "Notes", minLines = 3)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Select Icon", fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            iconOptions.chunked(5).forEach { row -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                row.forEach { ic -> val isSel = selectedIcon == ic
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isSel) Color.White else Color.White.copy(alpha = 0.1f)).clickable { selectedIcon = ic }, contentAlignment = Alignment.Center) {
                        Icon(imageVector = ic, contentDescription = null, tint = if (isSel) Color.Black else Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            } }
        }
        
        // Use fixed height instead of weight to prevent overlapping icon grid
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBackClick, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color.White), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("Cancel", fontFamily = PoppinsFamily) }
            Button(onClick = { 
                // Calculate precise timestamp by combining date and parsed time
                val baseMillis = datePickerState.selectedDateMillis ?: event?.timestamp ?: System.currentTimeMillis()
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    timeInMillis = baseMillis
                    
                    try {
                        val timeParser = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val parsedTime = timeParser.parse(selectedTime)
                        if (parsedTime != null) {
                            val timeCal = Calendar.getInstance().apply { time = parsedTime }
                            set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                            set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                        }
                    } catch (e: Exception) {
                        // Fallback to existing or current time if parsing fails
                    }
                }

                val newEvent = Event(
                    id = event?.id ?: java.util.UUID.randomUUID().toString(), 
                    userId = event?.userId ?: "",
                    title = title, 
                    date = selectedDateText, 
                    time = selectedTime, 
                    iconName = getNameForIcon(selectedIcon), 
                    location = location, 
                    notes = notes, 
                    timestamp = calendar.timeInMillis,
                    durationMinutes = durationMinutes,
                    parentCollectionId = event?.parentCollectionId ?: parentCollectionId
                )
                onCreateClick(newEvent) 
            },
                modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
                Text(text = if (event != null) "Save" else "Create", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
