package com.example.scheduler.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark
import com.example.scheduler.ui.theme.RustOrange
import com.example.scheduler.ui.theme.SchedulerTheme
import com.example.scheduler.ui.theme.SecondaryDark

/**
 * The primary dashboard of the application.
 * Displays a filtered, chronological view of the user's schedule, including 
 * individual events and organized collections.
 * 
 * @param userName The first name of the authenticated user to personalize the greeting.
 * @param events The raw list of active scheduled items from the database.
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
    
    /**
     * Dynamically filters the event list based on the user's selected time horizon.
     * Boundaries are explicitly calculated to follow standard calendar logic.
     */
    val filteredEvents = remember(events, selectedFilter) {
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val endOfToday = startOfToday + 24 * 60 * 60 * 1000L
        
        val startOfNextSunday = Calendar.getInstance().apply {
            // Start at today 00:00
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Roll forward to the next Sunday
            // If today is Sunday, we move 7 days forward to the *next* Sunday
            add(Calendar.DAY_OF_YEAR, 7 - (get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY))
        }.timeInMillis

        val startOfNextMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, 1)
        }.timeInMillis

        when (selectedFilter) {
            "Today" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < endOfToday }
            "This Week" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextSunday }
            "This Month" -> events.filter { it.timestamp != null && it.timestamp >= startOfToday && it.timestamp < startOfNextMonth }
            "Collections" -> events.filter { it.isCollection }
            else -> events
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            HeaderSection(name = userName, onProfileClick = onProfileClick)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            FilterAndSearchSection(
                selectedFilter = selectedFilter,
                onFilterClick = { isFilterExpanded = true },
                onSearchClick = { showSearchOverlay = true }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(filteredEvents) { event ->
                    if (event.isCollection) {
                        CollectionCard(event, onClick = { onCollectionClick(event) })
                    } else {
                        ExpandableEventCard(
                            event = event,
                            onEditClick = { onEditEvent(event) },
                            onDeleteClick = { eventToDelete = event }
                        )
                    }
                }
                
                item {
                    ArchivedEventsSection(onClick = onArchiveClick)
                }
            }
        }
        
        // Gradient overlay for scroll hint
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
        )

        // Task II.C.i (Create): Entry point for adding new data items to the database
        FloatingActionButton(
            onClick = { showCreateOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp),
            containerColor = Color.White,
            contentColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
        }

        if (showCreateOverlay) {
            CreateOverlay(
                onDismiss = { showCreateOverlay = false },
                onCreateEvent = {
                    showCreateOverlay = false
                    onCreateEvent()
                },
                onCreateCollection = {
                    showCreateOverlay = false
                    onCreateCollection()
                }
            )
        }
        
        if (showSearchOverlay) {
            SearchOverlay(
                allEvents = events,
                onDismiss = { showSearchOverlay = false },
                onEventClick = { /* Maybe highlight? For now just dismiss */ },
                onCollectionClick = { onCollectionClick(it) }
            )
        }

        if (isFilterExpanded) {
            FilterDropdown(
                userName = userName,
                onDismiss = { isFilterExpanded = false },
                onFilterSelected = { filter ->
                    selectedFilter = filter
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
                    TextButton(
                        onClick = {
                            onDeleteEvent(eventToDelete!!)
                            eventToDelete = null
                        }
                    ) {
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

@Composable
fun FilterAndSearchSection(
    selectedFilter: String,
    onFilterClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onFilterClick() }
        ) {
            Text(
                text = selectedFilter,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                color = Color.White
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Filter",
                tint = Color.White,
                modifier = Modifier.size(24.dp).padding(start = 4.dp)
            )
        }
        
        IconButton(onClick = onSearchClick) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun FilterDropdown(
    userName: String,
    onDismiss: () -> Unit,
    onFilterSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark.copy(alpha = 0.95f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            HeaderSection(name = userName)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "All Events",
                    fontFamily = PoppinsFamily,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Icon(Icons.Default.KeyboardArrowUp, null, tint = Color.White, modifier = Modifier.padding(start = 4.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    listOf("All Events", "Today", "This Week", "This Month", "Collections").forEach { filter ->
                        Text(
                            text = filter,
                            fontFamily = PoppinsFamily,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFilterSelected(filter) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        if (filter != "Collections") {
                            HorizontalDivider(color = Color.Black.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableEventCard(
    event: Event,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SecondaryDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(event.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    if (!expanded) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CalendarToday, null, Modifier.size(14.dp), Color.Black.copy(alpha = 0.6f))
                            Text(
                                text = " ${event.date}",
                                fontFamily = PoppinsFamily,
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Outlined.AccessTime, null, Modifier.size(14.dp), Color.Black.copy(alpha = 0.6f))
                            Text(
                                text = " ${event.time}",
                                fontFamily = PoppinsFamily,
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                
                Icon(
                    if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, null, Modifier.size(20.dp), Color.Black)
                        Text(
                            text = " ${event.date}",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Outlined.AccessTime, null, Modifier.size(20.dp), Color.Black)
                        Text(
                            text = " ${event.time}",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    DetailRow(Icons.Default.LocationOn, "Location", event.location ?: "None")
                    DetailRow(Icons.AutoMirrored.Filled.Notes, "Notes", event.notes ?: "None")
                    DetailRow(Icons.Default.Notifications, "Reminder", event.reminder ?: "None", showDivider = false)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, RustOrange),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RustOrange)
                        ) {
                            Icon(Icons.Default.DeleteOutline, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Delete", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                        }
                        
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryDark, contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Edit, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Edit", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String, showDivider: Boolean = true) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
        Icon(icon, null, Modifier.size(20.dp).padding(top = 2.dp), Color.Black)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(label, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
            Text(value, fontFamily = PoppinsFamily, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f))
            if (showDivider) {
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
fun CollectionCard(event: Event, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryDark),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(event.icon, null, tint = SecondaryDark, modifier = Modifier.size(20.dp))
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                
                val countText = when (event.eventCount ?: 0) {
                    0 -> "No Events"
                    1 -> "1 Event"
                    else -> "${event.eventCount} Events"
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!event.dateRange.isNullOrBlank()) {
                        Text(
                            text = "${event.dateRange} • ",
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = countText,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White)
        }
    }
}

@Composable
fun ArchivedEventsSection(onClick: () -> Unit = {}) {
    val density = LocalDensity.current
    val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.3f),
                    style = stroke,
                    cornerRadius = CornerRadius(with(density) { 16.dp.toPx() })
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Archive,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                "Archived Events",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                Icons.Default.KeyboardArrowRight,
                null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFF1F2E35)
fun ExpandedCardPreview() {
    SchedulerTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ExpandableEventCard(
                Event(
                    "2", "Team Meeting", "October 14", "12:00 PM", "Groups",
                    "Conference Room", "Bring design concepts!", "15 minutes before"
                )
            )
        }
    }
}
