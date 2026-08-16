package com.example.scheduler.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.scheduler.ui.theme.RustOrange
import com.example.scheduler.ui.theme.SecondaryDark

/**
 * Account management screen providing access to profile settings and alert preferences.
 * Includes a functional toggle for SMS notifications with user feedback.
 */
@Composable
fun ProfileScreen(
    user: User?,
    isSmsEnabled: Boolean,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onPersonalInfoClick: () -> Unit = {},
    onSecurityPrivacyClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onSmsToggle: (Boolean) -> Unit = {}
) {
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        
        // Header with navigation
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Account",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Dynamic profile summary based on current authenticated user
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SecondaryDark)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = user?.let { "${it.firstName} ${it.lastName}" } ?: "Guest User",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )
            
            Text(
                text = user?.email ?: "No email available",
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Preference management items
        SettingItem(Icons.Default.AccountCircle, "Personal Information", onClick = onPersonalInfoClick)
        SettingItem(
            icon = Icons.Default.Notifications, 
            label = "SMS Alerts",
            trailingContent = {
                Switch(
                    checked = isSmsEnabled,
                    onCheckedChange = { enabled ->
                        onSmsToggle(enabled)
                        feedbackMessage = if (enabled) {
                            "SMS Alerts enabled. You will now receive automated text notifications for your upcoming events."
                        } else {
                            "SMS Alerts disabled. You will no longer receive text notifications for events."
                        }
                        showFeedbackDialog = true
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = RustOrange,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        )
        SettingItem(Icons.Default.Security, "Security & Privacy", onClick = onSecurityPrivacyClick)
        SettingItem(Icons.Default.Info, "Help & Support", onClick = onHelpSupportClick)
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.1f),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Logout",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }

        if (showFeedbackDialog) {
            AlertDialog(
                onDismissRequest = { showFeedbackDialog = false },
                title = { 
                    Text(
                        text = if (isSmsEnabled) "Alerts Enabled" else "Alerts Disabled",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                text = { 
                    Text(
                        text = feedbackMessage,
                        fontFamily = PoppinsFamily
                    ) 
                },
                confirmButton = {
                    TextButton(onClick = { showFeedbackDialog = false }) {
                        Text("Dismiss", color = RustOrange, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                textContentColor = Color.Black,
                titleContentColor = Color.Black
            )
        }
    }
}
