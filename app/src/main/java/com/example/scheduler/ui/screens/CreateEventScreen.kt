package com.example.scheduler.ui.screens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark

/**
 * Form for creating or editing individual scheduled events.
 * Manages input validation and icon selection for data persistence.
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
    
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                return utcTimeMillis >= today
            }
        }
    )
    
    val selectedDateText = if (datePickerState.selectedDateMillis != null) {
        formatDate(datePickerState.selectedDateMillis)
    } else {
        event?.date ?: ""
    }

    var selectedTime by remember { mutableStateOf(event?.time ?: "12:00 PM") }
    val timeOptions = remember {
        (0..23).flatMap { hour ->
            listOf(0, 30).map { minute ->
                val ampm = if (hour < 12) "AM" else "PM"
                val displayHour = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                String.format(java.util.Locale.getDefault(), "%d:%02d %s", displayHour, minute, ampm)
            }
        }
    }

    val iconOptions = listOf(
        Icons.Default.Event,
        Icons.Default.Cake,
        Icons.Default.Groups,
        Icons.Default.CalendarToday,
        Icons.Default.Flight,
        Icons.Default.Restaurant,
        Icons.Default.School,
        Icons.Default.FitnessCenter,
        Icons.Default.Work,
        Icons.Default.Celebration,
        Icons.Default.LocalBar,
        Icons.Default.SportsEsports,
        Icons.Default.ShoppingCart,
        Icons.Default.Brush,
        Icons.Default.MusicNote
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = if (event != null) "Edit Event" else "New Event",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        StandardTextField(value = title, onValueChange = { title = it }, label = "Event Title")
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                StandardTextField(
                    value = selectedDateText,
                    onValueChange = {},
                    label = "Date",
                    readOnly = true,
                    onClick = { showDatePicker = true }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = showTimePicker,
                    onExpandedChange = { showTimePicker = !showTimePicker }
                ) {
                    StandardTextField(
                        value = selectedTime,
                        onValueChange = {},
                        label = "Time",
                        readOnly = true,
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        onClick = { showTimePicker = true }
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showTimePicker,
                        onDismissRequest = { showTimePicker = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        timeOptions.forEach { timeOption ->
                            DropdownMenuItem(
                                text = { Text(timeOption, fontFamily = PoppinsFamily) },
                                onClick = {
                                    selectedTime = timeOption
                                    showTimePicker = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = PrimaryDark,
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color.White.copy(alpha = 0.6f),
                    subheadContentColor = Color.White.copy(alpha = 0.6f),
                    yearContentColor = Color.White,
                    currentYearContentColor = Color.White,
                    selectedYearContentColor = Color.Black,
                    selectedYearContainerColor = Color.White,
                    dayContentColor = Color.White,
                    selectedDayContentColor = Color.Black,
                    selectedDayContainerColor = Color.White,
                    todayContentColor = Color.White,
                    todayDateBorderColor = Color.White
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false, // Ensure layout matches the clean navigable style
                    colors = DatePickerDefaults.colors(
                        containerColor = PrimaryDark,
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = Color.White.copy(alpha = 0.6f),
                        subheadContentColor = Color.White.copy(alpha = 0.6f),
                        yearContentColor = Color.White,
                        currentYearContentColor = Color.White,
                        selectedYearContentColor = Color.Black,
                        selectedYearContainerColor = Color.White,
                        dayContentColor = Color.White,
                        selectedDayContentColor = Color.Black,
                        selectedDayContainerColor = Color.White,
                        todayContentColor = Color.White,
                        todayDateBorderColor = Color.White
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = location, onValueChange = { location = it }, label = "Location")
        
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = notes, onValueChange = { notes = it }, label = "Notes", minLines = 3)

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Select Icon",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            iconOptions.chunked(5).forEach { rowIcons ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowIcons.forEach { icon ->
                        val isSelected = selectedIcon == icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.1f))
                                .clickable { selectedIcon = icon },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Black else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Cancel", fontFamily = PoppinsFamily)
            }
            
            Button(
                onClick = {
                    val newEvent = Event(
                        id = event?.id ?: java.util.UUID.randomUUID().toString(),
                        title = title,
                        date = selectedDateText,
                        time = selectedTime,
                        iconName = getNameForIcon(selectedIcon),
                        location = location,
                        notes = notes,
                        timestamp = datePickerState.selectedDateMillis ?: event?.timestamp,
                        parentCollectionId = event?.parentCollectionId ?: parentCollectionId
                    )
                    onCreateClick(newEvent)
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text(
                    text = if (event != null) "Save" else "Create",
                    fontFamily = PoppinsFamily, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
