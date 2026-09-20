package com.example.scheduler.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark

/**
 * Full-screen overlay providing filter options.
 */
@Composable
fun FilterDropdown(
    userName: String,
    onDismiss: () -> Unit,
    onFilterSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark.copy(alpha = 0.95f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            HeaderSection(name = userName)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "All Events",
                    fontFamily = PoppinsFamily,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Icon(Icons.Default.KeyboardArrowUp, null, tint = Color.White, modifier = Modifier.padding(start = 4.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    listOf("All Events", "Today", "This Week", "This Month", "Collections").forEach { filter ->
                        Text(
                            text = filter,
                            fontFamily = PoppinsFamily,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFilterSelected(filter) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                        if (filter != "Collections") {
                            HorizontalDivider(color = Color.Black.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}
