package com.example.scheduler.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.logic.formatMonthYear
import com.example.scheduler.ui.components.CreateSpeedDial
import com.example.scheduler.ui.components.ExpandableEventCard
import com.example.scheduler.ui.components.FilterAndSearchSection
import com.example.scheduler.ui.components.FilterDropdown
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange
import java.util.Calendar

/**
 * Screen for viewing all upcoming events grouped by month with collapsible month headers.
 */
@Composable
fun ViewAllEventsScreen(
    userName: String,
    events: List<Event>,
    onBackClick: () -> Unit,
    onEditEvent: (Event) -> Unit = {},
    onDeleteEvent: (Event) -> Unit = {},
    onCreateEvent: () -> Unit = {},
    onCreateCollection: () -> Unit = {}
) {
    var showSearchOverlay by remember { mutableStateOf(false) }
    var isFilterExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Events") }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }

    val filteredEvents = remember(events, selectedFilter) {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val startOfToday = now.timeInMillis
        val endOfToday = startOfToday + 24 * 60 * 60 * 1000L
        val startOfNextSun = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7 - (get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY)) }.timeInMillis
        val startOfNextMonth = (now.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, 1) }.timeInMillis

        when (selectedFilter) {
            "Today" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < endOfToday }
            "This Week" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextSun }
            "This Month" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextMonth }
            else -> events
        }
    }

    val currentMonthName = remember { formatMonthYear(System.currentTimeMillis()) }

    val groupedEvents = remember(filteredEvents) {
        filteredEvents
            .filter { !it.isCollection }
            .sortedBy { it.timestamp ?: Long.MAX_VALUE }
            .groupBy { formatMonthYear(it.timestamp) }
    }

    // Current month is expanded by default, future months start collapsed
    val expandedMonths = remember(groupedEvents) {
        mutableStateMapOf<String, Boolean>().apply {
            groupedEvents.keys.forEach { month ->
                this[month] = (month == currentMonthName || groupedEvents.keys.firstOrNull() == month)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "All Events",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            FilterAndSearchSection(
                selectedFilter = selectedFilter,
                onFilterClick = { isFilterExpanded = true },
                onSearchClick = { showSearchOverlay = true },
                placeholderText = "Search events..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (groupedEvents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No events scheduled",
                        fontFamily = PoppinsFamily,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 160.dp)
                ) {
                    groupedEvents.forEach { (monthName, monthEvents) ->
                        item(key = monthName) {
                            val isExpanded = expandedMonths[monthName] ?: false

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedMonths[monthName] = !isExpanded }
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = monthName,
                                        fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${monthEvents.size} " + if (monthEvents.size == 1) "event" else "events",
                                            fontFamily = PoppinsFamily,
                                            fontSize = 13.sp,
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = if (isExpanded) "Collapse $monthName" else "Expand $monthName",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        monthEvents.forEach { event ->
                                            ExpandableEventCard(
                                                event = event,
                                                onEditClick = { onEditEvent(event) },
                                                onDeleteClick = { eventToDelete = event }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        CreateSpeedDial(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            onCreateEvent = onCreateEvent,
            onCreateCollection = onCreateCollection
        )

        if (showSearchOverlay) {
            SearchOverlay(
                userName = userName,
                allEvents = events,
                onDismiss = { showSearchOverlay = false },
                onEventClick = { onEditEvent(it) },
                onCollectionClick = {},
                onDeleteEvent = { eventToDelete = it }
            )
        }

        if (isFilterExpanded) {
            FilterDropdown(
                userName = userName,
                onDismiss = { isFilterExpanded = false },
                onFilterSelected = {
                    selectedFilter = it
                    isFilterExpanded = false
                }
            )
        }

        if (eventToDelete != null) {
            AlertDialog(
                onDismissRequest = { eventToDelete = null },
                title = { Text("Delete Event", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete '${eventToDelete?.title}'?", fontFamily = PoppinsFamily) },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteEvent(eventToDelete!!)
                        eventToDelete = null
                    }) {
                        Text("Delete", color = RustOrange, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { eventToDelete = null }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                containerColor = Color.White,
                textContentColor = Color.Black,
                titleContentColor = Color.Black
            )
        }
    }
}
