package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange

/**
 * Detailed view of a specific collection, allowing users to manage contained events.
 * Provides functionality to rename, delete, and add existing events to the folder.
 */
@Composable
fun CollectionDetailScreen(
    title: String,
    collection: Event? = null,
    events: List<Event>? = null, // Current events in this collection
    allManageableEvents: List<Event> = emptyList(),
    collectionMap: Map<String, String> = emptyMap(),
    onBackClick: () -> Unit,
    onCreateEvent: () -> Unit = {},
    onCreateCollection: () -> Unit = {},
    onEditEvent: (Event) -> Unit = {},
    onDeleteEvent: (Event) -> Unit = {},
    onRenameCollection: (Event, String) -> Unit = { _, _ -> },
    onDeleteCollection: (Event) -> Unit = {},
    onManageCollectionEvents: (List<Event>) -> Unit = {}
) {
    var showCreateOverlay by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showManageDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(title) }
    
    // Dynamically retrieve the current set of events for this specific collection
    val currentEvents = events ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Header navigation with back arrow
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Icon(
                        Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Content management controls for collections
                if (collection != null) {
                    Row {
                        IconButton(onClick = { showManageDialog = true }) {
                            Icon(Icons.Default.LibraryAdd, contentDescription = "Manage Content", tint = Color.White)
                        }
                        IconButton(onClick = { 
                            newName = title
                            showRenameDialog = true 
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename", tint = Color.White)
                        }
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Collection", tint = Color.White)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 100.dp)
            ) {
                currentEvents.forEach { event ->
                    ExpandableEventCard(
                        event = event,
                        onEditClick = { onEditEvent(event) },
                        onDeleteClick = { eventToDelete = event }
                    )
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

        // FAB (Optional, but keeping for consistency)
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

        if (showManageDialog) {
            ManageContentDialog(
                currentCollection = collection!!,
                allManageableEvents = allManageableEvents,
                collectionMap = collectionMap,
                onDismiss = { showManageDialog = false },
                onConfirm = { selected ->
                    onManageCollectionEvents(selected)
                    showManageDialog = false
                }
            )
        }

        if (showRenameDialog && collection != null) {
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text("Rename Collection", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Enter a new name for your collection:", fontFamily = PoppinsFamily)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            placeholder = { Text("Collection Name") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RustOrange,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (newName.isNotBlank()) {
                                onRenameCollection(collection, newName)
                                showRenameDialog = false
                            }
                        }
                    ) {
                        Text("Rename", color = RustOrange, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text("Cancel", color = Color.Black)
                    }
                },
                containerColor = Color.White,
                textContentColor = Color.Black,
                titleContentColor = Color.Black
            )
        }

        if (showDeleteConfirm && collection != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete Collection", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete the entire collection '${collection.title}'?", fontFamily = PoppinsFamily) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteCollection(collection)
                            showDeleteConfirm = false
                            onBackClick() // Go back after deleting the entire collection
                        }
                    ) {
                        Text("Delete", color = RustOrange, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) {
                        Text("Cancel", color = Color.Black)
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
fun ManageContentDialog(
    currentCollection: Event,
    allManageableEvents: List<Event>,
    collectionMap: Map<String, String>,
    onDismiss: () -> Unit,
    onConfirm: (List<Event>) -> Unit
) {
    val selectedEvents = remember { 
        mutableStateListOf<Event>().apply {
            addAll(allManageableEvents.filter { it.parentCollectionId == currentCollection.id })
        } 
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Collection Content", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
        text = {
            if (allManageableEvents.isEmpty()) {
                Text("No active events found.", fontFamily = PoppinsFamily)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    allManageableEvents.forEach { event ->
                        val currentParentId = event.parentCollectionId
                        val isSelected = selectedEvents.any { it.id == event.id }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) {
                                        selectedEvents.removeAll { it.id == event.id }
                                    } else {
                                        selectedEvents.add(event)
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked == true) {
                                        selectedEvents.add(event)
                                    } else {
                                        selectedEvents.removeAll { it.id == event.id }
                                    }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = RustOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(event.title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${event.date} • ${event.time}", fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.Gray)
                                    if (currentParentId != null && currentParentId != currentCollection.id) {
                                        val folderName = collectionMap[currentParentId] ?: "Another Folder"
                                        Text(" • In $folderName", fontFamily = PoppinsFamily, fontSize = 12.sp, color = RustOrange, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedEvents.toList()) }
            ) {
                Text("Save Changes", color = RustOrange, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Black)
            }
        },
        containerColor = Color.White,
        textContentColor = Color.Black,
        titleContentColor = Color.Black
    )
}
