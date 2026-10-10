package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.ui.components.CollectionCard
import com.example.scheduler.ui.components.CreateSpeedDial
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * Screen for viewing all active collections formatted as full-width summary cards.
 */
@Composable
fun ViewAllCollectionsScreen(
    collections: List<Event>,
    onBackClick: () -> Unit,
    onCollectionClick: (Event) -> Unit,
    onCreateEvent: () -> Unit = {},
    onCreateCollection: () -> Unit = {}
) {
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
                    text = "All Collections",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (collections.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No collections created yet",
                        fontFamily = PoppinsFamily,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 160.dp)
                ) {
                    items(collections) { collection ->
                        CollectionCard(
                            event = collection,
                            onClick = { onCollectionClick(collection) }
                        )
                    }
                }
            }
        }

        CreateSpeedDial(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            onCreateEvent = onCreateEvent,
            onCreateCollection = onCreateCollection
        )
    }
}
