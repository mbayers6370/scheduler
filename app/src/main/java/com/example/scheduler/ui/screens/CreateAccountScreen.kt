package com.example.scheduler.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scheduler.ui.components.PrimaryButton
import com.example.scheduler.ui.components.StandardTextField
import com.example.scheduler.ui.theme.PoppinsFamily
import com.example.scheduler.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

/**
 * Screen for registering a new user matching the create account mockup.
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
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitRegistration = {
        when {
            listOf(firstName, lastName, email, username, password).any { it.isBlank() } -> {
                errorMessage = "Please fill in all fields"
            }
            password.length < 6 -> {
                errorMessage = "Password must be at least 6 characters"
            }
            !email.contains("@") || !email.contains(".") -> {
                errorMessage = "Please enter a valid email address"
            }
            else -> {
                keyboardController?.hide()
                scope.launch {
                    if (viewModel.registerUser(username, password, firstName, lastName, email)) {
                        onAccountCreated()
                    } else {
                        errorMessage = "Username or email already registered"
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackToLogin, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text(
                text = "Create account",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Enter your details to get started.",
            fontFamily = PoppinsFamily,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StandardTextField(
                value = firstName,
                onValueChange = { firstName = it; errorMessage = null },
                labelAbove = "First name",
                placeholder = "First name",
                contentType = ContentType.PersonFirstName,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.weight(1f)
            )
            StandardTextField(
                value = lastName,
                onValueChange = { lastName = it; errorMessage = null },
                labelAbove = "Last name",
                placeholder = "Last name",
                contentType = ContentType.PersonLastName,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        StandardTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            labelAbove = "Email",
            placeholder = "you@example.com",
            contentType = ContentType.EmailAddress,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        StandardTextField(
            value = username,
            onValueChange = { username = it; errorMessage = null },
            labelAbove = "Username",
            placeholder = "Choose a username",
            contentType = ContentType.Username,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(16.dp))

        StandardTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            labelAbove = "Password",
            placeholder = "Create a password",
            visualTransformation = PasswordVisualTransformation(),
            contentType = ContentType.NewPassword,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { submitRegistration() }
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Use at least 6 characters.",
            fontFamily = PoppinsFamily,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f)
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                fontFamily = PoppinsFamily,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Create account",
            onClick = { submitRegistration() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "Already have an account? ",
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "Log in",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}
