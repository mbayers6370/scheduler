package com.example.scheduler.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.Event
import com.example.scheduler.logic.formatDate
import com.example.scheduler.logic.icon
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange
import com.example.scheduler.ui.theme.SecondaryDark
import java.util.Calendar

/**
 * A reactive card component that expands to reveal detailed event information.
 */
@Composable
fun ExpandableEventCard(
    event: Event,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val isToday = remember(event.timestamp) {
        if (event.timestamp == null) false
        else {
            val eventCal = Calendar.getInstance().apply { timeInMillis = event.timestamp }
            val nowCal = Calendar.getInstance()
            eventCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
            eventCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
        }
    }

    val displayDate = remember(event.date, event.timestamp) {
        if (event.date.isNotBlank()) event.date else formatDate(event.timestamp)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isToday) BorderStroke(1.5.dp, RustOrange) else null
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
                        .background(if (isToday) RustOrange.copy(alpha = 0.15f) else SecondaryDark.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = event.icon,
                        contentDescription = null,
                        tint = if (isToday) RustOrange else SecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
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
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isToday) RustOrange else Color.Black.copy(alpha = 0.6f)
                            )
                            Text(
                                text = if (isToday) " Today" else " $displayDate",
                                fontFamily = PoppinsFamily,
                                fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 12.sp,
                                color = if (isToday) RustOrange else Color.Black.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Outlined.AccessTime, null, Modifier.size(14.dp), Color.Black.copy(alpha = 0.6f))
                            Text(
                                text = " ${event.time}",
                                fontFamily = PoppinsFamily,
                                fontSize = 12.sp,
                                color = Color.Black.copy(alpha = 0.6f)
                            )
                            if (!event.location.isNullOrBlank()) {
                                Text(
                                    text = " • ${event.location}",
                                    fontFamily = PoppinsFamily,
                                    fontSize = 12.sp,
                                    color = Color.Black.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
                
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse details for ${event.title}" else "Expand details for ${event.title}",
                    tint = Color.Black
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, null, Modifier.size(20.dp), Color.Black)
                        Text(
                            text = " $displayDate",
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
                    
                    val details = remember(event) {
                        mutableListOf<Pair<ImageVector, Pair<String, String>>>().apply {
                            add(Icons.Default.Timer to ("Duration" to formatDuration(event.durationMinutes)))
                            if (!event.location.isNullOrBlank()) {
                                add(Icons.Default.LocationOn to ("Location" to event.location))
                            }
                            if (!event.notes.isNullOrBlank()) {
                                add(Icons.AutoMirrored.Filled.Notes to ("Notes" to event.notes))
                            }
                            if (!event.reminder.isNullOrBlank()) {
                                add(Icons.Default.Notifications to ("Reminder" to event.reminder))
                            }
                        }
                    }

                    details.forEachIndexed { index, (icon, labelAndValue) ->
                        val (label, value) = labelAndValue
                        DetailRow(
                            icon = icon,
                            label = label,
                            value = value,
                            showDivider = index < details.lastIndex
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = onEditClick,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryDark, contentColor = Color.White)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Edit Event",
                                    fontFamily = PoppinsFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        TextButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            colors = ButtonDefaults.textButtonColors(contentColor = RustOrange.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.DeleteOutline, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Delete Event", fontFamily = PoppinsFamily, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
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

private fun formatDuration(durationMinutes: Int): String {
    if (durationMinutes <= 0) return "60 mins"
    val hours = durationMinutes / 60
    val mins = durationMinutes % 60
    return when {
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins} mins"
    }
}
