package com.anix.app.ui.screens.auth
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.AccentLime
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Secondary
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ANIX",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = Primary,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(BorderStroke(3.dp, BorderBlack), RoundedCornerShape(12.dp))
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            NeoTextField(
                value = email,
                onValueChange = { email = it; error = null },
                label = "Email",
                placeholder = "Enter your email",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeoTextField(
                value = password,
                onValueChange = { password = it; error = null },
                label = "Password",
                placeholder = "Enter your password",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            NeoButton(
                text = "Login",
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        error = "Please fill all fields"
                        return@NeoButton
                    }
                    isLoading = true
                    error = null
                    scope.launch {
                        val result = ServiceLocator.getAuthRepository().login(email, password)
                        isLoading = false
                        result.fold(
                            onSuccess = { onLoginSuccess() },
                            onFailure = { error = it.message ?: "Login failed" }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
            NeoButton(
                text = "Continue with Google",
                onClick = { error = "Google login coming soon" },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.White,
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Don't have an account? Register",
                modifier = Modifier.clickable { onNavigateToRegister() },
                style = MaterialTheme.typography.bodyMedium,
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            LoadingIndicator()
        }
    }
}
