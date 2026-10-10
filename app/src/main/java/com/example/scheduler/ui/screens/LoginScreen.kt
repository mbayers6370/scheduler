package com.example.scheduler.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
 * Screen for existing users to authenticate matching the login mockup.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitLogin = {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields"
        } else {
            keyboardController?.hide()
            scope.launch {
                if (viewModel.loginUser(username, password)) onLoginSuccess()
                else errorMessage = "Invalid username or password"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = "Welcome back",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Text(
            text = "Sign in to manage your events.",
            fontFamily = PoppinsFamily,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        StandardTextField(
            value = username,
            onValueChange = { 
                username = it
                errorMessage = null 
            },
            labelAbove = "Email or username",
            placeholder = "Enter email or username",
            contentType = ContentType.Username,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        StandardTextField(
            value = password,
            onValueChange = { 
                password = it
                errorMessage = null
            },
            labelAbove = "Password",
            placeholder = "Enter password",
            visualTransformation = PasswordVisualTransformation(),
            contentType = ContentType.Password,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { submitLogin() }
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Forgot password?",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { showForgotPasswordDialog = true }
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
            text = "Log in",
            onClick = { submitLogin() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = "New here? ",
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "Create account",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                onDismiss = { showForgotPasswordDialog = false },
                onResetPassword = { id, newPass, callback ->
                    scope.launch {
                        val success = viewModel.resetPassword(id, newPass)
                        callback(success)
                    }
                }
            )
        }
    }
}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onResetPassword: (String, String, (Boolean) -> Unit) -> Unit
) {
    var identifier by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    text = "Enter your registered username or email, along with your new password.",
                    fontFamily = PoppinsFamily,
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it; dialogError = null },
                    label = { Text("Username or Email", fontFamily = PoppinsFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; dialogError = null },
                    label = { Text("New Password", fontFamily = PoppinsFamily) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                if (dialogError != null) {
                    Text(text = dialogError!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
                if (successMsg != null) {
                    Text(text = successMsg!!, color = Color(0xFF2E7D32), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (identifier.isBlank() || newPassword.isBlank()) {
                        dialogError = "Please fill in all fields"
                        return@TextButton
                    }
                    onResetPassword(identifier, newPassword) { success ->
                        if (success) {
                            successMsg = "Password reset successfully! You can now log in."
                        } else {
                            dialogError = "No account found matching that username or email."
                        }
                    }
                }
            ) {
                Text("Reset Password", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        },
        containerColor = Color.White,
        textContentColor = Color.Black,
        titleContentColor = Color.Black
    )
}
