package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark
import com.example.scheduler.ui.theme.SecondaryDark

/**
 * Search interface for querying scheduled items by name.
 * Provides immediate feedback as the user types and direct navigation to matching items.
 */
@Composable
fun SearchOverlay(
    allEvents: List<Event>,
    onDismiss: () -> Unit,
    onEventClick: (Event) -> Unit,
    onCollectionClick: (Event) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Real-time filtering logic based on input query
    val filteredResults = remember(searchQuery, allEvents) {
        if (searchQuery.isBlank()) emptyList<Event>()
        else allEvents.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark.copy(alpha = 0.95f))
            .clickable(enabled = false) { /* Overlay background interaction disabled for content focus */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            HeaderSection(name = "Matt")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Persistent search input field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            "Search Events...", 
                            fontFamily = PoppinsFamily, 
                            fontSize = 20.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        ) 
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f)
                    ),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp, 
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Light
                    )
                )
                
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            Icons.Default.Close, 
                            contentDescription = "Clear", 
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(filteredResults) { item ->
                    SearchResultCard(
                        item = item,
                        onClick = {
                            if (item.isCollection) onCollectionClick(item)
                            else onEventClick(item)
                            onDismiss()
                        }
                    )
                }
            }
        }

        // Center Cancel at the bottom
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .clickable { onDismiss() }
        ) {
            Icon(
                Icons.Default.Close, 
                contentDescription = null, 
                tint = Color.White.copy(alpha = 0.5f), 
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Cancel",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Light,
                fontSize = 32.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SearchResultCard(
    item: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(if (item.isCollection) Color.White else SecondaryDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = if (item.isCollection) PrimaryDark else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = item.title,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                if (!item.isCollection) {
                    Text(
                        text = item.date,
                        fontFamily = PoppinsFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = "Collection",
                        fontFamily = PoppinsFamily,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
