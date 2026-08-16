package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * View for accessing historical scheduled items.
 * Groups archived events by month and year to provide an organized historical record.
 */
@Composable
fun ArchiveScreen(
    archivedEvents: List<Event>,
    onBackClick: () -> Unit,
    onMonthClick: (String) -> Unit
) {
    // Dynamically generate collection summaries based on archived event timestamps
    val months = remember(archivedEvents) {
        archivedEvents
            .groupBy { formatMonthYear(it.timestamp) }
            .map { (monthName, events) ->
                Event(
                    id = monthName,
                    title = monthName,
                    date = "",
                    time = "",
                    iconName = "Folder",
                    isCollection = true,
                    dateRange = "", // Could calculate range if needed
                    eventCount = events.size,
                    timestamp = events.firstOrNull()?.timestamp
                )
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // Header with Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Archive",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Icon(
                Icons.Default.Archive,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Past Collections",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 100.dp)
        ) {
            months.forEach { month ->
                CollectionCard(month, onClick = { onMonthClick(month.title) })
            }
        }
    }
}
