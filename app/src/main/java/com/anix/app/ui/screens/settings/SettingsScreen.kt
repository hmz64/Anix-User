package com.anix.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var username by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("← Back", modifier = Modifier.clickable { onBack() }, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("Account", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Text("Update Username", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoTextField(value = username, onValueChange = { username = it }, placeholder = "New username", modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoButton(text = "Save", onClick = { viewModel.updateUsername(username) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth(), enabled = !uiState.isLoading)
                    if (uiState.successMessage != null) { Text(uiState.successMessage!!, color = Color.Green) }
                    if (uiState.error != null) { Text(uiState.error!!, color = Color.Red) }
                }
            }
            item {
                Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFFFE0E0), RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, Color.Red), RoundedCornerShape(8.dp)).padding(12.dp)) {
                    Text("Danger Zone", fontWeight = FontWeight.Bold, color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoButton(text = "Logout", onClick = onLogout, backgroundColor = Color.Red, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
