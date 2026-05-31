package com.anix.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.TextPrimary
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField

object PendingGoogleRegistration {
    var idToken: String = ""
    var email: String = ""
    var name: String = ""
    var picture: String = ""
}

@Composable
fun GoogleRegisterScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: GoogleRegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }

    val idToken = PendingGoogleRegistration.idToken
    val email = PendingGoogleRegistration.email
    val name = PendingGoogleRegistration.name
    val picture = PendingGoogleRegistration.picture

    if (idToken.isBlank()) {
        onBack()
        return
    }

    if (uiState.success != null) {
        PendingGoogleRegistration.idToken = ""
        PendingGoogleRegistration.email = ""
        PendingGoogleRegistration.name = ""
        PendingGoogleRegistration.picture = ""
        onSuccess()
        return
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
                    text = "Satu Langkah Lagi!",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Pilih username untuk akun Google-mu",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )

                Text(
                    text = email,
                    color = Color.Gray,
                    fontSize = 13.sp,
                )

                NeoTextField(
                    value = username,
                    onValueChange = { username = it; usernameError = null },
                    placeholder = "Username",
                    modifier = Modifier.fillMaxWidth()
                )
                if (usernameError != null) {
                    Text(usernameError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.Start))
                }

                if (uiState.error != null) {
                    Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                NeoButton(
                    text = if (uiState.isLoading) "Loading..." else "Daftar",
                    onClick = {
                        if (username.length < 3) {
                            usernameError = "Username minimal 3 karakter"
                        } else {
                            viewModel.completeRegistration(idToken, username)
                        }
                    },
                    backgroundColor = Primary,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                NeoButton(
                    text = "Kembali",
                    onClick = onBack,
                    backgroundColor = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )
            }
        }
    }
}
