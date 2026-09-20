package com.example.scheduler.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.logic.icon
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.SecondaryDark

/**
 * Card component for displaying a summary of an event collection folder.
 */
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
