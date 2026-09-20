package com.example.scheduler.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.components.StandardTextField
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * Screen for registering a new user.
 */
@Composable
fun CreateAccountScreen(
    viewModel: AuthViewModel,
    onAccountCreated: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        IconButton(onClick = onBackToLogin, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
        }
        Text("Create Account", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color.White)
        
        Spacer(modifier = Modifier.height(8.dp))
        Text("Enter your details to get started", fontFamily = PoppinsFamily, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(24.dp))

        StandardTextField(value = firstName, onValueChange = { firstName = it; errorMessage = null }, label = "First Name")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = lastName, onValueChange = { lastName = it; errorMessage = null }, label = "Last Name")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = email, onValueChange = { email = it; errorMessage = null }, label = "Email")
        Spacer(modifier = Modifier.height(16.dp))
        StandardTextField(value = username, onValueChange = { username = it; errorMessage = null }, label = "Username")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password", fontFamily = PoppinsFamily) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (listOf(firstName, lastName, email, username, password).any { it.isBlank() }) {
                    errorMessage = "Please fill in all fields"
                    return@Button
                }
                scope.launch {
                    if (viewModel.registerUser(username, password, firstName, lastName, email)) onAccountCreated()
                    else errorMessage = "Username already exists"
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
        ) {
            Text("Create Account", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBackToLogin, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Already have an account? Login", color = Color.White, fontFamily = PoppinsFamily)
        }
    }
}
