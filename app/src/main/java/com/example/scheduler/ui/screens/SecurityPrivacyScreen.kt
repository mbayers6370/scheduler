package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.theme.RustOrange

/**
 * Configuration screen for managing account security settings and data privacy options.
 */
@Composable
fun SecurityPrivacyScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "Security",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        SettingItem(Icons.Default.Lock, "Change Password")
        SettingItem(Icons.Default.Security, "Two-Factor Authentication")
        
        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = { /* Handle Delete Account */ },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 48.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = RustOrange, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Delete Account",
                fontFamily = PoppinsFamily,
                color = RustOrange,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
