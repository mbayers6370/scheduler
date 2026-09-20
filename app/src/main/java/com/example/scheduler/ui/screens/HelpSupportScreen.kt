package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.components.SettingItem
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * Screen providing support resources.
 */
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(40.dp))
        IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
        Text("Help", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White)
        Spacer(modifier = Modifier.height(32.dp))
        SettingItem(Icons.Default.QuestionAnswer, "Frequently Asked Questions")
        SettingItem(Icons.Default.Email, "Contact Us")
        SettingItem(Icons.Default.Info, "Privacy Policy")
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Version 1.0.0", fontFamily = PoppinsFamily, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
        }
    }
}
