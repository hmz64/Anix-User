package com.anix.app.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anix.app.BuildConfig
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var privacyMode by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }
    var pushEnabled by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteConfirmText by remember { mutableStateOf("") }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showBannerPicker by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (showAvatarPicker) {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = java.io.File(context.cacheDir, "avatar_upload.jpg")
                file.outputStream().use { output -> inputStream?.copyTo(output) }
                inputStream?.close()
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
                viewModel.updateAvatar(part)
            } else if (showBannerPicker) {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = java.io.File(context.cacheDir, "banner_upload.jpg")
                file.outputStream().use { output -> inputStream?.copyTo(output) }
                inputStream?.close()
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
                viewModel.updateBanner(part)
            }
        }
    }

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

            // === Photo Profile ===
            item { SectionHeader("Photo Profile") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = ServiceLocator.getAuthRepository().toString(),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(100.dp).clip(CircleShape).border(BorderStroke(3.dp, BorderBlack), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Pick from Gallery", onClick = { showAvatarPicker = true; imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, backgroundColor = Primary)
                    }
                }
            }

            // === Banner Profile ===
            item { SectionHeader("Banner Profile") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Primary).clip(RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp)))
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Pick Banner from Gallery", onClick = { showBannerPicker = true; imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // === Update Username ===
            item { SectionHeader("Username") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        NeoTextField(value = username, onValueChange = { if (it.length <= 20) username = it.replace(Regex("[^a-zA-Z0-9_]"), "") }, placeholder = "New username", modifier = Modifier.fillMaxWidth(), singleLine = true)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            if (username.isNotEmpty() && (username.length < 3 || username.length > 20)) Text("3-20 chars, alphanumeric", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                            Text("${username.length}/20", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Save", onClick = { viewModel.updateUsername(username) }, backgroundColor = Primary, enabled = username.length in 3..20)
                    }
                }
            }

            // === Bio ===
            item { SectionHeader("Bio") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        NeoTextField(value = bio, onValueChange = { if (it.length <= 150) bio = it }, placeholder = "Tell us about yourself...", modifier = Modifier.fillMaxWidth(), singleLine = false)
                        Text("${bio.length}/150", modifier = Modifier.align(Alignment.End), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Save Bio", onClick = { viewModel.updateBio(bio) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // === Change Password ===
            item { SectionHeader("Change Password") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        NeoTextField(value = oldPassword, onValueChange = { oldPassword = it }, placeholder = "Old Password", modifier = Modifier.fillMaxWidth(), visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = { Text(if (showOldPassword) "Hide" else "Show", modifier = Modifier.clickable { showOldPassword = !showOldPassword }.padding(8.dp), style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.Bold) })
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoTextField(value = newPassword, onValueChange = { newPassword = it }, placeholder = "New Password", modifier = Modifier.fillMaxWidth(), visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = { Text(if (showNewPassword) "Hide" else "Show", modifier = Modifier.clickable { showNewPassword = !showNewPassword }.padding(8.dp), style = MaterialTheme.typography.labelSmall, color = Primary, fontWeight = FontWeight.Bold) })
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Confirm New Password", modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
                        if (newPassword.isNotEmpty() && newPassword.length < 8) Text("Min 8 characters", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) Text("Passwords do not match", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Change Password", onClick = { viewModel.updatePassword(oldPassword, newPassword) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth(), enabled = newPassword.length >= 8 && newPassword == confirmPassword)
                    }
                }
            }

            // === Privacy ===
            item { SectionHeader("Privacy") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ToggleRow("Privacy Mode", privacyMode, { privacyMode = it; viewModel.updatePrivacy(if (it) "private" else "public") })
                        ToggleRow("Show in Leaderboard", showLeaderboard, { showLeaderboard = it })
                        ToggleRow("Push Notifications", pushEnabled, { pushEnabled = it })
                    }
                }
            }

            // === About ===
            item { SectionHeader("About") }
            item {
                NeoCard {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Version: ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Privacy Policy", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://anix.app/privacy"))) })
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Terms of Service", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://anix.app/terms"))) })
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Clear Cache", onClick = { com.anix.app.ui.components.AsyncImage::class }, backgroundColor = Surface, textColor = Color.Black, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Send Feedback", onClick = {
                            context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@anix.app")
                                putExtra(Intent.EXTRA_SUBJECT, "Anix Feedback")
                            })
                        }, backgroundColor = Surface, textColor = Color.Black, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // === Danger Zone ===
            item { SectionHeader("Danger Zone") }
            item {
                NeoCard(modifier = Modifier.border(BorderStroke(2.dp, Color.Red), RoundedCornerShape(8.dp))) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        NeoButton(text = "Logout", onClick = { showLogoutConfirm = true }, backgroundColor = Color.Red, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        NeoButton(text = "Delete Account", onClick = { showDeleteConfirm = true }, backgroundColor = Color.Red, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        if (uiState.successMessage != null) {
            Text(uiState.successMessage!!, color = Color.Green, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
        }
        if (uiState.error != null) {
            Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.Black)
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange, colors = androidx.compose.material3.SwitchDefaults.colors(checkedTrackColor = Primary))
    }
}
