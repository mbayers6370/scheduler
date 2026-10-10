package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.logic.icon
import com.example.scheduler.ui.components.ExpandableEventCard
import com.example.scheduler.ui.components.HeaderSection
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark

/**
 * Overlay for searching scheduled items with comprehensive search states and rich metadata differentiation.
 */
@Composable
fun SearchOverlay(
    userName: String,
    allEvents: List<Event>,
    onDismiss: () -> Unit,
    onEventClick: (Event) -> Unit,
    onCollectionClick: (Event) -> Unit,
    onDeleteEvent: (Event) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }

    val collectionsMap = remember(allEvents) {
        allEvents.filter { it.isCollection }.associate { it.id to it.title }
    }
    val filteredResults = remember(searchQuery, allEvents) {
        if (searchQuery.isBlank()) emptyList()
        else allEvents.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            (it.location?.contains(searchQuery, ignoreCase = true) == true) ||
            (it.notes?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(PrimaryDark).clickable(enabled = false) {}) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(64.dp))
            HeaderSection(name = userName)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.offset(x = (-8).dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to dashboard", tint = Color.White.copy(alpha = 0.9f))
                }
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = if (!isSearchFocused && searchQuery.isBlank()) {
                        { Text("Search events...", fontFamily = PoppinsFamily, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White.copy(alpha = 0.5f)) }
                    } else null,
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isSearchFocused = it.isFocused },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search query", tint = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, fontFamily = PoppinsFamily, fontWeight = FontWeight.Light)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            when {
                searchQuery.isBlank() && !isSearchFocused -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Search Scheduled Items",
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Search across your events, collections, locations, and notes.",
                                fontFamily = PoppinsFamily,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                searchQuery.isBlank() && isSearchFocused -> {
                    // Blank state while typing/focused - ready for search results
                }
                filteredResults.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Results Found",
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No items matched '$searchQuery'. Try checking for typos or searching with a different term.",
                                fontFamily = PoppinsFamily,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 160.dp)) {
                        items(filteredResults) { item ->
                            if (item.isCollection) {
                                val parentName = item.parentCollectionId?.let { collectionsMap[it] }
                                SearchResultCard(
                                    item = item,
                                    parentCollectionName = parentName,
                                    onClick = {
                                        onCollectionClick(item)
                                        onDismiss()
                                    }
                                )
                            } else {
                                ExpandableEventCard(
                                    event = item,
                                    onEditClick = {
                                        onEventClick(item)
                                        onDismiss()
                                    },
                                    onDeleteClick = {
                                        onDeleteEvent(item)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    item: Event,
    parentCollectionName: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                
                val metaText = if (item.isCollection) {
                    val count = item.eventCount ?: 0
                    "Collection • $count " + if (count == 1) "Event" else "Events"
                } else {
                    val baseStr = "${item.date} • ${item.time}"
                    val locStr = if (!item.location.isNullOrBlank()) " • ${item.location}" else ""
                    val colStr = if (!parentCollectionName.isNullOrBlank()) " • In '$parentCollectionName'" else ""
                    baseStr + locStr + colStr
                }
                Text(text = metaText, fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
            }
            Icon(
                imageVector = if (item.isCollection) Icons.AutoMirrored.Filled.KeyboardArrowRight else Icons.Default.Edit,
                contentDescription = if (item.isCollection) "View Collection" else "Edit Event",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
