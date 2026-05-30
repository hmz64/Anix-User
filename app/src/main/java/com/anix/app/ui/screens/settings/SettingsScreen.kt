package com.anix.app.ui.screens.settings

import android.app.Application
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch
import com.anix.app.BuildConfig
import com.anix.app.core.di.PreferencesKeys
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.core.theme.TextPrimary
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.theme.GlassError
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.GlassBorder
import com.anix.app.core.util.liquidGlass
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard
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

    val user = uiState.user

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

    LaunchedEffect(user) {
        user?.let {
            username = it.username
            bio = it.bio
            privacyMode = it.privacySetting == "private"
            showLeaderboard = it.showLeaderboard
            pushEnabled = it.pushEnabled
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            if (showAvatarPicker) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val ext = uri.lastPathSegment?.substringAfterLast('.') ?: "jpg"
                val file = java.io.File(context.cacheDir, "avatar_upload_${System.currentTimeMillis()}.$ext")
                file.outputStream().use { output -> inputStream?.copyTo(output) }
                inputStream?.close()
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
                viewModel.updateAvatar(part)
            } else if (showBannerPicker) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val ext = uri.lastPathSegment?.substringAfterLast('.') ?: "jpg"
                val file = java.io.File(context.cacheDir, "banner_upload_${System.currentTimeMillis()}.$ext")
                file.outputStream().use { output -> inputStream?.copyTo(output) }
                inputStream?.close()
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
                viewModel.updateBanner(part)
            }
        }
        showAvatarPicker = false
        showBannerPicker = false
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = { showLogoutConfirm = false; onLogout() }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; deleteConfirmText = "" },
            title = { Text("Delete Account") },
            text = {
                Column {
                    Text("Enter your username to confirm deletion:")
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoTextField(
                        value = deleteConfirmText,
                        onValueChange = { deleteConfirmText = it },
                        placeholder = "Username",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (deleteConfirmText == user?.username) {
                            viewModel.deleteAccount(deleteConfirmText)
                            showDeleteConfirm = false
                            deleteConfirmText = ""
                        }
                    },
                    enabled = deleteConfirmText == user?.username
                ) {
                    Text("Delete", color = if (deleteConfirmText == user?.username) Color.Red else Color.Gray)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false; deleteConfirmText = "" }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF0A1628).copy(alpha = 0.95f)).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("← Back", modifier = Modifier.clickable { onBack() }, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        if (uiState.isLoading && user == null) {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { SectionHeader("Photo Profile") }
                item {
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = ApiClient.resolveUrl(user?.avatar),
                                contentDescription = "Avatar",
                                modifier = Modifier.size(100.dp).clip(CircleShape).border(BorderStroke(3.dp, BorderBlack), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Pick from Gallery", onClick = { showAvatarPicker = true; imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, backgroundColor = Primary)
                        }
                    }
                }

                item { SectionHeader("Banner Profile") }
                item {
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Primary).clip(RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))) {
                                if (!user?.banner.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ApiClient.resolveUrl(user?.banner),
                                        contentDescription = "Banner",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Pick Banner from Gallery", onClick = { showBannerPicker = true; imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                item { SectionHeader("Username") }
                item {
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp)) {
                            NeoTextField(value = username, onValueChange = { if (it.length <= 20) username = it.replace(Regex("[^a-zA-Z0-9_]"), "") }, placeholder = "New username", modifier = Modifier.fillMaxWidth(), singleLine = true)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                if (username.isNotEmpty() && (username.length < 3 || username.length > 20)) Text("3-20 chars, alphanumeric", color = GlassError, style = MaterialTheme.typography.bodySmall)
                                Text("${username.length}/20", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Save", onClick = { viewModel.updateUsername(username) }, backgroundColor = Primary, enabled = username.length in 3..20)
                        }
                    }
                }

                item { SectionHeader("Bio") }
                item {
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp)) {
                            NeoTextField(value = bio, onValueChange = { if (it.length <= 150) bio = it }, placeholder = "Tell us about yourself...", modifier = Modifier.fillMaxWidth(), singleLine = false)
                            Text("${bio.length}/150", modifier = Modifier.align(Alignment.End), style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Save Bio", onClick = { viewModel.updateBio(bio) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

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
                            if (newPassword.isNotEmpty() && newPassword.length < 8) Text("Min 8 characters", color = GlassError, style = MaterialTheme.typography.bodySmall)
                            if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) Text("Passwords do not match", color = GlassError, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Change Password", onClick = { viewModel.updatePassword(oldPassword, newPassword) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth(), enabled = newPassword.length >= 8 && newPassword == confirmPassword)
                        }
                    }
                }

                item { SectionHeader("Privacy") }
                item {
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp)) {
                            ToggleRow("Privacy Mode", privacyMode, { privacyMode = it; viewModel.updatePrivacy(if (it) "private" else "public") })
                            ToggleRow("Show in Leaderboard", showLeaderboard, { showLeaderboard = it; viewModel.updateLeaderboard(it) })
                            ToggleRow("Push Notifications", pushEnabled, { pushEnabled = it; viewModel.updatePushEnabled(it) })
                        }
                    }
                }

                item { SectionHeader("Comments") }
                item {
                    val scope = rememberCoroutineScope()
                    val ds = ServiceLocator.getDataStore()
                    val bannerPref by ds?.data?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }
                    val showBannerPref = bannerPref?.get(PreferencesKeys.SHOW_COMMENT_BANNERS) ?: true
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp)) {
                            ToggleRow("Tampilkan Banner di Komentar", showBannerPref, {
                                scope.launch {
                                    ds?.edit { prefs ->
                                        prefs[PreferencesKeys.SHOW_COMMENT_BANNERS] = it
                                    }
                                }
                            })
                        }
                    }
                }

                item { SectionHeader("About") }
                item {
                    NeoCard {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Version: ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Privacy Policy", color = AccentBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://anix.app/privacy"))) })
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Terms of Service", color = AccentBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://anix.app/terms"))) })
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Clear Cache", onClick = { viewModel.clearCache(context.applicationContext as Application) }, backgroundColor = Surface, textColor = Color.Black, modifier = Modifier.fillMaxWidth())
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

                item { SectionHeader("Danger Zone") }
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassError.copy(alpha = 0.08f))
                            .border(1.dp, GlassError.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            NeoButton(text = "Logout", onClick = { showLogoutConfirm = true }, backgroundColor = Color.Red, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(8.dp))
                            NeoButton(text = "Delete Account", onClick = { showDeleteConfirm = true }, backgroundColor = Color.Red, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            if (uiState.successMessage != null) {
                Text(uiState.successMessage!!, color = Color.Green, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
            }
            if (uiState.error != null) {
                Text(uiState.error!!, color = Color.Red, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange, colors = androidx.compose.material3.SwitchDefaults.colors(checkedTrackColor = Primary))
    }
}
