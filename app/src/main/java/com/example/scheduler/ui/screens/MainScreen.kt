package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scheduler.data.model.Event
import com.example.scheduler.ui.components.*
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange
import java.util.Calendar

/**
 * Main dashboard screen of the application.
 */
@Composable
fun MainScreen(
    userName: String,
    events: List<Event>,
    onCollectionClick: (Event) -> Unit = {},
    onCreateEvent: () -> Unit = {},
    onCreateCollection: () -> Unit = {},
    onEditEvent: (Event) -> Unit = {},
    onDeleteEvent: (Event) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {}
) {
    var showCreateOverlay by remember { mutableStateOf(false) }
    var showSearchOverlay by remember { mutableStateOf(false) }
    var isFilterExpanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Events") }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    
    val filteredEvents = remember(events, selectedFilter) {
        val now = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
        val startOfToday = now.timeInMillis
        val endOfToday = startOfToday + 24 * 60 * 60 * 1000L
        val startOfNextSun = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7 - (get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY)) }.timeInMillis
        val startOfNextMonth = (now.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1); add(Calendar.MONTH, 1) }.timeInMillis

        when (selectedFilter) {
            "Today" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < endOfToday }
            "This Week" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextSun }
            "This Month" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextMonth }
            "Collections" -> events.filter { it.isCollection }
            else -> events
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(64.dp))
            HeaderSection(name = userName, onProfileClick = onProfileClick)
            Spacer(modifier = Modifier.height(8.dp))
            FilterAndSearchSection(selectedFilter = selectedFilter, onFilterClick = { isFilterExpanded = true }, onSearchClick = { showSearchOverlay = true })
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 100.dp)) {
                items(filteredEvents) { event ->
                    if (event.isCollection) CollectionCard(event, onClick = { onCollectionClick(event) })
                    else ExpandableEventCard(event = event, onEditClick = { onEditEvent(event) }, onDeleteClick = { eventToDelete = event })
                }
                item { ArchivedEventsSection(onClick = onArchiveClick) }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(100.dp).background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background))))
        FloatingActionButton(onClick = { showCreateOverlay = true }, modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp), containerColor = Color.White, contentColor = Color.Black, shape = CircleShape) {
            Icon(Icons.Default.Add, "Add", modifier = Modifier.size(32.dp))
        }
        if (showCreateOverlay) CreateOverlay(onDismiss = { showCreateOverlay = false }, onCreateEvent = { showCreateOverlay = false; onCreateEvent() }, onCreateCollection = { showCreateOverlay = false; onCreateCollection() })
        if (showSearchOverlay) SearchOverlay(allEvents = events, onDismiss = { showSearchOverlay = false }, onEventClick = {}, onCollectionClick = { onCollectionClick(it) })
        if (isFilterExpanded) FilterDropdown(userName = userName, onDismiss = { isFilterExpanded = false }, onFilterSelected = { selectedFilter = it; isFilterExpanded = false })
        if (eventToDelete != null) {
            AlertDialog(onDismissRequest = { eventToDelete = null }, title = { Text("Delete Event", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) }, text = { Text("Are you sure you want to delete '${eventToDelete?.title}'?", fontFamily = PoppinsFamily) },
                confirmButton = { TextButton(onClick = { onDeleteEvent(eventToDelete!!); eventToDelete = null }) { Text("Delete", color = RustOrange, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { eventToDelete = null }) { Text("Cancel", color = Color.Gray) } }, containerColor = Color.White, textContentColor = Color.Black, titleContentColor = Color.Black)
        }
    }
}
