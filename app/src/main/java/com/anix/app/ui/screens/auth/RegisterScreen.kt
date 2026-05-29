package com.anix.app.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.anix.app.core.theme.BorderBlack
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
    var localError by remember { mutableStateOf<String?>(null) }

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

            NeoTextField(value = username, onValueChange = { username = it; localError = null }, placeholder = "Username", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            NeoTextField(value = email, onValueChange = { email = it; localError = null }, placeholder = "Email", modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            NeoTextField(
                value = password, onValueChange = { password = it; localError = null },
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
            Spacer(modifier = Modifier.height(12.dp))
            NeoTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it; localError = null },
                placeholder = "Confirm Password", modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(8.dp))

            val errorMsg = localError ?: uiState.error
            if (errorMsg != null) {
                Text(errorMsg, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            NeoButton(
                text = if (uiState.isLoading) "Loading..." else "Register",
                onClick = {
                    when {
                        username.length < 3 -> localError = "Username must be at least 3 characters"
                        !email.contains("@") -> localError = "Invalid email"
                        password.length < 8 -> localError = "Password must be at least 8 characters"
                        password != confirmPassword -> localError = "Passwords do not match"
                        else -> viewModel.register(username, email, password)
                    }
                },
                backgroundColor = Primary, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Already have an account? Login", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLoginClick() })
        }
    }
}
