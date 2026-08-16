package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark

/**
 * Full-screen modal overlay for selecting the type of item to create.
 * Provides a clear choice between adding a single event or a new collection folder.
 */
@Composable
fun CreateOverlay(
    onDismiss: () -> Unit,
    onCreateEvent: () -> Unit = {},
    onCreateCollection: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryDark.copy(alpha = 0.95f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Create :",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            OverlayOption(Icons.Default.Event, "Event", onClick = onCreateEvent)
            OverlayOption(Icons.Default.Folder, "Collection", onClick = onCreateCollection)
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
fun OverlayOption(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .clickable { onClick() }
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 36.sp,
            color = Color.White
        )
    }
}
