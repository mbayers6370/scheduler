package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.theme.PoppinsFamily

/**
 * Detailed profile editing screen.
 * Displays the current user's persistent information and allows for local modifications.
 * 
 * @param user The current User entity retrieved from the database.
 */
@Composable
fun PersonalInformationScreen(
    user: User?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    // Initializing state with current user data to ensure pre-population
    var firstName by remember { mutableStateOf(user?.firstName ?: "") }
    var lastName by remember { mutableStateOf(user?.lastName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Navigation header
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.offset(x = (-12).dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "Personal Info",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input fields for all profile attributes
        StandardTextField(value = firstName, onValueChange = { firstName = it }, label = "First Name")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = lastName, onValueChange = { lastName = it }, label = "Last Name")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = email, onValueChange = { email = it }, label = "Email")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = username, onValueChange = { username = it }, label = "Username")

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Save Changes",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
