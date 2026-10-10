package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onArchiveClick: () -> Unit = {},
    onViewAllEventsClick: () -> Unit = {},
    onViewAllCollectionsClick: () -> Unit = {}
) {
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
            else -> events
        }
    }

    val collections = remember(filteredEvents) { filteredEvents.filter { it.isCollection } }
    val standaloneEvents = remember(filteredEvents) { filteredEvents.filter { !it.isCollection } }
    val mainDashboardEvents = remember(standaloneEvents) { standaloneEvents.take(4) }
    val mainDashboardCollections = remember(collections) { collections.take(4) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(64.dp))
            HeaderSection(name = userName, onProfileClick = onProfileClick)
            Spacer(modifier = Modifier.height(16.dp))
            FilterAndSearchSection(selectedFilter = selectedFilter, onFilterClick = { isFilterExpanded = true }, onSearchClick = { showSearchOverlay = true })
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 160.dp)) {
                if (standaloneEvents.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Upcoming Events",
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onViewAllEventsClick() }
                            ) {
                                Text(
                                    text = "View all",
                                    fontFamily = PoppinsFamily,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View all events",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    items(mainDashboardEvents) { event ->
                        ExpandableEventCard(event = event, onEditClick = { onEditEvent(event) }, onDeleteClick = { eventToDelete = event })
                    }
                }

                if (collections.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Collections",
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onViewAllCollectionsClick() }
                            ) {
                                Text(
                                    text = "View all",
                                    fontFamily = PoppinsFamily,
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "View all collections",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    item {
                        val chunkedCollections = mainDashboardCollections.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            chunkedCollections.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowItems.forEach { col ->
                                        CollectionGridCard(
                                            event = col,
                                            modifier = Modifier.weight(1f),
                                            onClick = { onCollectionClick(col) }
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onArchiveClick() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = "View Archived Events",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Archived Events",
                            fontFamily = PoppinsFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(100.dp).background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background))))
        CreateSpeedDial(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            onCreateEvent = onCreateEvent,
            onCreateCollection = onCreateCollection
        )
        if (showSearchOverlay) SearchOverlay(userName = userName, allEvents = events, onDismiss = { showSearchOverlay = false }, onEventClick = { onEditEvent(it) }, onCollectionClick = { onCollectionClick(it) }, onDeleteEvent = { eventToDelete = it })
        if (isFilterExpanded) FilterDropdown(userName = userName, onDismiss = { isFilterExpanded = false }, onFilterSelected = { selectedFilter = it; isFilterExpanded = false })
        if (eventToDelete != null) {
            AlertDialog(onDismissRequest = { eventToDelete = null }, title = { Text("Delete Event", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) }, text = { Text("Are you sure you want to delete '${eventToDelete?.title}'?", fontFamily = PoppinsFamily) },
                confirmButton = { TextButton(onClick = { onDeleteEvent(eventToDelete!!); eventToDelete = null }) { Text("Delete", color = RustOrange, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { eventToDelete = null }) { Text("Cancel", color = Color.Gray) } }, containerColor = Color.White, textContentColor = Color.Black, titleContentColor = Color.Black)
        }
    }
}
