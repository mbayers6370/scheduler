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
import com.example.scheduler.logic.icon
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange
import com.example.scheduler.ui.theme.SecondaryDark

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
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
