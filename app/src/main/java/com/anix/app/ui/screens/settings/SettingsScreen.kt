package com.anix.app.ui.screens.settings
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var privacyMode by remember { mutableStateOf(false) }
    var bannerUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "← Settings",
            modifier = Modifier.clickable { onBack() },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Update Username
        Text("Update Username", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        NeoTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = "New username",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        NeoButton(
            text = "Save Username",
            onClick = {
                scope.launch {
                    ServiceLocator.getUserRepository().updateName(username)
                }
            },
            backgroundColor = Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Update Banner
        Text("Update Banner URL", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        NeoTextField(
            value = bannerUrl,
            onValueChange = { bannerUrl = it },
            placeholder = "Banner image URL",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        NeoButton(
            text = "Save Banner",
            onClick = {
                scope.launch {
                    ServiceLocator.getUserRepository().updateBanner(bannerUrl)
                }
            },
            backgroundColor = Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Privacy Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Hide your activity from others", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Switch(
                checked = privacyMode,
                onCheckedChange = {
                    privacyMode = it
                    scope.launch {
                        ServiceLocator.getUserRepository().updatePrivacy(it)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Info
        Text("App Info", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Anix v1.0.0", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text("Neo Brutalism Edition", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        NeoButton(
            text = "Back",
            onClick = onBack,
            backgroundColor = Surface,
            textColor = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
