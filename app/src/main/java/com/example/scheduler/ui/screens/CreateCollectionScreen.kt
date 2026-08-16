package com.example.scheduler.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.util.Calendar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark

/**
 * Form for initializing a new collection (folder) of events.
 * Allows users to define a title and an associated date range for the group.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCollectionScreen(
    onBackClick: () -> Unit,
    onCreateClick: (Event) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val dateRangePickerState = rememberDateRangePickerState(
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

    val dateRangeText = if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
        formatDateRange(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis)
    } else {
        ""
    }

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
            text = "New Collection",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        StandardTextField(value = name, onValueChange = { name = it }, label = "Collection Name")
        
        Spacer(modifier = Modifier.height(16.dp))

        StandardTextField(
            value = dateRangeText,
            onValueChange = {},
            label = "Date Range",
            readOnly = true,
            onClick = { showDatePicker = true }
        )

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
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.height(450.dp), // Height adjusted for layout matching
                    title = {
                        Text(
                            text = "Select dates",
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                        )
                    },
                    headline = {
                        DateRangePickerDefaults.DateRangePickerHeadline(
                            selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                            selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                            displayMode = dateRangePickerState.displayMode,
                            dateFormatter = DatePickerDefaults.dateFormatter(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, bottom = 12.dp)
                        )
                    },
                    showModeToggle = false, // Match the clean 'new event' layout
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
                        todayDateBorderColor = Color.White,
                        dayInSelectionRangeContentColor = Color.Black,
                        dayInSelectionRangeContainerColor = Color.White.copy(alpha = 0.4f)
                    )
                )
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
                    val newCollection = Event(
                        id = java.util.UUID.randomUUID().toString(),
                        title = name,
                        date = "",
                        time = "",
                        iconName = "Folder",
                        isCollection = true,
                        dateRange = dateRangeText,
                        eventCount = 0,
                        timestamp = dateRangePickerState.selectedStartDateMillis
                    )
                    onCreateClick(newCollection)
                },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Create", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
