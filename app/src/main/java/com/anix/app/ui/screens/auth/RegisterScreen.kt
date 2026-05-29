package com.anix.app.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.Primary
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField

@Composable
fun RegisterScreen(
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    if (uiState.registerSuccess != null) {
        onRegisterSuccess()
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Register", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            NeoTextField(value = username, onValueChange = { username = it; usernameError = null }, placeholder = "Username", modifier = Modifier.fillMaxWidth())
            if (usernameError != null) {
                Text(usernameError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
            }
            Spacer(modifier = Modifier.height(12.dp))

            NeoTextField(value = email, onValueChange = { email = it; emailError = null }, placeholder = "Email", modifier = Modifier.fillMaxWidth())
            if (emailError != null) {
                Text(emailError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
            }
            Spacer(modifier = Modifier.height(12.dp))

            NeoTextField(
                value = password, onValueChange = { password = it; passwordError = null },
                placeholder = "Password", modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        if (showPassword) "Hide" else "Show",
                        modifier = Modifier.clickable { showPassword = !showPassword }.padding(8.dp),
                        style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.Bold
                    )
                }
            )
            if (passwordError != null) {
                Text(passwordError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
            }
            Spacer(modifier = Modifier.height(12.dp))

            NeoTextField(value = confirmPassword, onValueChange = { confirmPassword = it; confirmError = null }, placeholder = "Confirm Password", modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
            if (confirmError != null) {
                Text(confirmError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Password strength indicator
            if (password.isNotEmpty()) {
                val strength = when {
                    password.length < 4 -> 0.25f
                    password.length < 6 -> 0.5f
                    password.length < 8 -> 0.75f
                    else -> 1.0f
                }
                val color = when {
                    strength <= 0.25f -> Color.Red
                    strength <= 0.5f -> Color(0xFFFFA500)
                    strength < 1.0f -> Color(0xFFFFD700)
                    else -> Color(0xFF4CAF50)
                }
                Text(
                    text = when {
                        strength <= 0.25f -> "Weak"
                        strength <= 0.5f -> "Fair"
                        strength < 1.0f -> "Good"
                        else -> "Strong"
                    },
                    color = color, style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.error != null) {
                Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            NeoButton(
                text = if (uiState.isLoading) "Loading..." else "Register",
                onClick = {
                    var valid = true
                    if (username.length < 3) { usernameError = "Username must be at least 3 characters"; valid = false }
                    if (email.isBlank()) { emailError = "Email is required"; valid = false }
                    else if (!email.contains("@")) { emailError = "Invalid email"; valid = false }
                    if (password.length < 8) { passwordError = "Password must be at least 8 characters"; valid = false }
                    if (password != confirmPassword) { confirmError = "Passwords do not match"; valid = false }
                    if (valid) viewModel.register(username, email, password)
                },
                backgroundColor = Primary, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Already have an account? Login", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLoginClick() })
        }
    }
}
