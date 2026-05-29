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
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    if (uiState.loginSuccess != null) {
        onLoginSuccess()
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState())) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            NeoTextField(
                value = email, onValueChange = { email = it; emailError = null },
                placeholder = "Email", modifier = Modifier.fillMaxWidth()
            )
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
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.error != null) {
                Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            NeoButton(
                text = if (uiState.isLoading) "Loading..." else "Login",
                onClick = {
                    var valid = true
                    if (email.isBlank()) { emailError = "Email is required"; valid = false }
                    else if (!email.contains("@")) { emailError = "Invalid email format"; valid = false }
                    if (password.isBlank()) { passwordError = "Password is required"; valid = false }
                    if (valid) viewModel.login(email, password)
                },
                backgroundColor = Primary, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Don't have an account? Register", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onRegisterClick() })
        }
    }
}
