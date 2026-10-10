package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
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
import com.example.scheduler.ui.components.CollectionCard
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * Screen for viewing historical archived events.
 */
@Composable
fun ArchiveScreen(
    archivedEvents: List<Event>,
    onBackClick: () -> Unit,
    onMonthClick: (String) -> Unit
) {
    val months = remember(archivedEvents) {
        archivedEvents.groupBy { formatMonthYear(it.timestamp) }.map { (name, evts) ->
            val first = evts.firstOrNull()
            Event(
                id = name,
                userId = first?.userId ?: "",
                title = name,
                date = "",
                time = "",
                iconName = "Folder",
                isCollection = true,
                eventCount = evts.size,
                timestamp = first?.timestamp
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Archive", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.Archive, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Archived Events", fontFamily = PoppinsFamily, fontSize = 18.sp, color = Color.White.copy(alpha = 0.85f))
        Spacer(modifier = Modifier.height(16.dp))
        
        if (months.isEmpty()) {
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
                            .background(Color.White.copy(alpha = 0.1f), shape = androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "No Archived Events",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Archived Events Yet",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Events from past months will automatically be archived and organized here.",
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 100.dp)) {
                months.forEach { month -> CollectionCard(month, onClick = { onMonthClick(month.title) }) }
            }
        }
    }
}
