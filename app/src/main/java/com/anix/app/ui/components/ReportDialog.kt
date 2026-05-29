package com.anix.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (type: String, message: String) -> Unit
) {
    val options = listOf(
        "video_salah" to "Video salah",
        "subtitle_bermasalah" to "Subtitle bermasalah"
    )
    var selectedType by remember { mutableStateOf("video_salah") }
    var customText by remember { mutableStateOf("") }
    var useCustom by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Laporkan Masalah", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = value; useCustom = false }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == value && !useCustom,
                            onClick = { selectedType = value; useCustom = false }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { useCustom = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = useCustom,
                        onClick = { useCustom = true }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Tulis sendiri", style = MaterialTheme.typography.bodyMedium)
                }

                if (useCustom) {
                    Spacer(Modifier.height(8.dp))
                    NeoTextField(
                        value = customText,
                        onValueChange = { customText = it },
                        placeholder = "Jelaskan masalah...",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(
                        if (useCustom) "custom" else selectedType,
                        if (useCustom) customText else ""
                    )
                    onDismiss()
                }
            ) {
                Text("Kirim", fontWeight = FontWeight.Bold, color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray)
            }
        }
    )
}
