package com.example.scheduler.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.data.model.User
import com.example.scheduler.ui.components.SettingItem
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange
import com.example.scheduler.ui.theme.SecondaryDark

/**
 * Screen for managing account settings.
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

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text(
                text = "Account",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(SecondaryDark)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = user?.let { "${it.firstName} ${it.lastName}".trim() }.takeIf { !it.isNullOrBlank() } ?: "Matt Bayers",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = user?.email.takeIf { !it.isNullOrBlank() } ?: "email@example.com",
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION: PROFILE
        Text(
            text = "PROFILE",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SecondaryDark.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingItem(
                icon = Icons.Outlined.Person,
                label = "Personal Information",
                onClick = onPersonalInfoClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: PREFERENCES
        Text(
            text = "PREFERENCES",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SecondaryDark.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            SettingItem(
                icon = Icons.Outlined.Notifications,
                label = "SMS Reminders",
                subtitle = "30 minutes before events",
                trailingContent = {
                    Switch(
                        checked = isSmsEnabled,
                        onCheckedChange = { enabled ->
                            onSmsToggle(enabled)
                            feedbackMessage = if (enabled) "SMS Reminders enabled. You will receive automated text notifications."
                            else "SMS Reminders disabled. Notifications paused."
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
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: SUPPORT & SECURITY
        Text(
            text = "SUPPORT & SECURITY",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SecondaryDark.copy(alpha = 0.5f)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingItem(
                    icon = Icons.Outlined.Security,
                    label = "Security & Privacy",
                    onClick = onSecurityPrivacyClick
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                SettingItem(
                    icon = Icons.Outlined.HelpOutline,
                    label = "Help & Support",
                    onClick = onHelpSupportClick
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            Text(
                text = "Log out",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
        if (showFeedbackDialog) {
            AlertDialog(
                onDismissRequest = { showFeedbackDialog = false },
                title = { Text(if (isSmsEnabled) "Alerts Enabled" else "Alerts Disabled", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
                text = { Text(feedbackMessage, fontFamily = PoppinsFamily) },
                confirmButton = { TextButton(onClick = { showFeedbackDialog = false }) { Text("Dismiss", color = RustOrange, fontWeight = FontWeight.Bold) } },
                containerColor = Color.White, textContentColor = Color.Black, titleContentColor = Color.Black
            )
        }
    }
}
