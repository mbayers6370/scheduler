package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.PrimaryDark

/**
 * Onboarding screen for permissions.
 */
@Composable
fun OnboardingPermissionsScreen(
    onEnableClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(PrimaryDark).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.NotificationsActive, null, tint = Color.White, modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text("Stay on Schedule", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Enable SMS alerts to receive automated notifications 30 minutes before your events begin.", fontFamily = PoppinsFamily, fontSize = 16.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center, lineHeight = 24.sp)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onEnableClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)) {
            Text("Enable Alerts", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onSkipClick) { Text("Skip for now", fontFamily = PoppinsFamily, color = Color.White.copy(alpha = 0.5f), fontWeight = FontWeight.Medium) }
    }
}
