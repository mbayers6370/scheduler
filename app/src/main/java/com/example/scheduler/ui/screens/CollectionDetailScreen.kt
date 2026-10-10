package com.example.scheduler.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.ui.components.CreateSpeedDial
import com.example.scheduler.ui.components.ExpandableEventCard
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange

/**
 * Screen displaying the details and contents of a specific collection.
 */
@Composable
fun CollectionDetailScreen(
    title: String,
    collection: Event? = null,
    events: List<Event>? = null,
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
    var eventToDelete by remember { mutableStateOf<Event?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showManageDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(title) }
    val currentEvents = events ?: emptyList()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(40.dp))
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White, modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.width(8.dp)); Icon(Icons.Default.Folder, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
                }
                if (collection != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showManageDialog = true }) { Icon(Icons.Default.LibraryAdd, "Manage Content", tint = Color.White) }
                        Box {
                            IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, "More Options", tint = Color.White) }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                containerColor = Color.White
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Rename Collection", fontFamily = PoppinsFamily, color = Color.Black) },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black) },
                                    onClick = {
                                        showMenu = false
                                        newName = title
                                        showRenameDialog = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Collection", fontFamily = PoppinsFamily, color = RustOrange, fontWeight = FontWeight.SemiBold) },
                                    leadingIcon = { Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = RustOrange) },
                                    onClick = {
                                        showMenu = false
                                        showDeleteConfirm = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (currentEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color.White.copy(alpha = 0.1f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = "Empty Collection",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Collection is Empty",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This collection has no events yet.",
                            fontFamily = PoppinsFamily,
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (collection != null && allManageableEvents.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = { showManageDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                            ) {
                                Icon(Icons.Default.LibraryAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Events to Collection", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 160.dp)) {
                    currentEvents.forEach { event -> ExpandableEventCard(event = event, onEditClick = { onEditEvent(event) }, onDeleteClick = { eventToDelete = event }) }
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(100.dp).background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background))))
        CreateSpeedDial(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            onCreateEvent = onCreateEvent,
            onCreateCollection = onCreateCollection
        )
        if (eventToDelete != null) {
            AlertDialog(onDismissRequest = { eventToDelete = null }, title = { Text("Delete Event", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) }, text = { Text("Are you sure?", fontFamily = PoppinsFamily) },
                confirmButton = { TextButton(onClick = { onDeleteEvent(eventToDelete!!); eventToDelete = null }) { Text("Delete", color = RustOrange, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { eventToDelete = null }) { Text("Cancel", color = Color.Gray) } }, containerColor = Color.White, textContentColor = Color.Black, titleContentColor = Color.Black)
        }
        if (showManageDialog) ManageContentDialog(currentCollection = collection!!, allManageableEvents = allManageableEvents, collectionMap = collectionMap, onDismiss = { showManageDialog = false }, onConfirm = { selected -> onManageCollectionEvents(selected); showManageDialog = false })
        if (showRenameDialog && collection != null) {
            AlertDialog(onDismissRequest = { showRenameDialog = false }, title = { Text("Rename Collection", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
                text = { Column { Text("Enter a new name:", fontFamily = PoppinsFamily); Spacer(modifier = Modifier.height(16.dp)); OutlinedTextField(value = newName, onValueChange = { newName = it }, placeholder = { Text("Collection Name") }, modifier = Modifier.fillMaxWidth()) } },
                confirmButton = { TextButton(onClick = { if (newName.isNotBlank()) { onRenameCollection(collection, newName); showRenameDialog = false } }) { Text("Rename", color = RustOrange, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { showRenameDialog = false }) { Text("Cancel", color = Color.Black) } }, containerColor = Color.White, textContentColor = Color.Black, titleContentColor = Color.Black)
        }
        if (showDeleteConfirm && collection != null) {
            AlertDialog(onDismissRequest = { showDeleteConfirm = false }, title = { Text("Delete Collection", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) }, text = { Text("Delete entire collection?", fontFamily = PoppinsFamily) },
                confirmButton = { TextButton(onClick = { onDeleteCollection(collection); showDeleteConfirm = false; onBackClick() }) { Text("Delete", color = RustOrange, fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel", color = Color.Black) } }, containerColor = Color.White, textContentColor = Color.Black, titleContentColor = Color.Black)
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
    val selectedEvents = remember { mutableStateListOf<Event>().apply { addAll(allManageableEvents.filter { it.parentCollectionId == currentCollection.id }) } }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Manage Collection Content", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select events to include in '${currentCollection.title}'. Unchecking an event removes it from this collection.",
                    fontFamily = PoppinsFamily,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        text = {
            if (allManageableEvents.isEmpty()) {
                Text("No active events found.", fontFamily = PoppinsFamily)
            } else {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(rememberScrollState())) {
                    allManageableEvents.forEach { event ->
                        val isSel = selectedEvents.any { it.id == event.id }
                        val isOriginalMember = event.parentCollectionId == currentCollection.id
                        val otherFolderName = if (event.parentCollectionId != null && !isOriginalMember) collectionMap[event.parentCollectionId] ?: "Another Folder" else null

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSel) selectedEvents.removeAll { it.id == event.id }
                                    else selectedEvents.add(event)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSel,
                                onCheckedChange = { checked ->
                                    if (checked == true) selectedEvents.add(event)
                                    else selectedEvents.removeAll { it.id == event.id }
                                },
                                colors = CheckboxDefaults.colors(checkedColor = RustOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${event.date} • ${event.time}", fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.Gray)
                                }
                                val statusText = when {
                                    isSel && isOriginalMember -> "In this collection"
                                    isSel && otherFolderName != null -> "Will move from '$otherFolderName'"
                                    isSel -> "Will add to '${currentCollection.title}'"
                                    otherFolderName != null -> "In '$otherFolderName' (check to move)"
                                    else -> "Unassigned"
                                }
                                val statusColor = when {
                                    isSel -> RustOrange
                                    otherFolderName != null -> Color.Gray
                                    else -> Color.Gray.copy(alpha = 0.7f)
                                }
                                Text(
                                    text = statusText,
                                    fontFamily = PoppinsFamily,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = statusColor
                                )
                            }
                        }
                        HorizontalDivider(color = Color.Black.copy(alpha = 0.08f))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedEvents.toList()) }) {
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
