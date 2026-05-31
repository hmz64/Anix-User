package com.anix.app.ui.screens.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.TextPrimary
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onGoogleRegister: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.initGoogleSignIn(context)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { viewModel.handleGoogleResult(it) }
            } catch (e: ApiException) {
                // user cancelled or error
            }
        }
    }

    if (uiState.loginSuccess != null) {
        onLoginSuccess()
        return
    }

    LaunchedEffect(uiState.googleNeedsRegistration) {
        uiState.googleNeedsRegistration?.let { info ->
            PendingGoogleRegistration.idToken = info.idToken
            PendingGoogleRegistration.email = info.email
            PendingGoogleRegistration.name = info.name
            PendingGoogleRegistration.picture = info.picture
            viewModel.clearGoogleNeedsRegistration()
            onGoogleRegister()
        }
    }

    Scaffold(containerColor = Color.Transparent) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Login",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                NeoTextField(
                    value = email, onValueChange = { email = it; emailError = null },
                    placeholder = "Email", modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) {
                    Text(emailError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                }

                NeoTextField(
                    value = password, onValueChange = { password = it; passwordError = null },
                    placeholder = "Password", modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        TextButton(onClick = { showPassword = !showPassword }) {
                            Text(
                                if (showPassword) "Hide" else "Show",
                                color = AccentBlue
                            )
                        }
                    }
                )
                if (passwordError != null) {
                    Text(passwordError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                }

                if (uiState.error != null) {
                    Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.Gray.copy(alpha = 0.3f))

                Text(
                    text = "or continue with",
                    color = Color.Gray,
                    fontSize = 13.sp,
                )

                NeoButton(
                    text = "Sign in with Google",
                    onClick = { viewModel.getGoogleSignInIntent()?.let { googleLauncher.launch(it) } },
                    backgroundColor = Color(0xFF4285F4),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                )

                TextButton(onClick = onRegisterClick) {
                    Text(
                        "Don't have an account? Register",
                        color = AccentBlue,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
