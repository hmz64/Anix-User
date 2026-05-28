package com.anix.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.core.theme.TextBlack

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(8.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.border(2.dp, if (isError) Color.Red else BorderBlack, shape),
        shape = shape,
        label = if (label.isNotEmpty()) {
            { Text(label, fontWeight = FontWeight.Bold) }
        } else null,
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, color = TextBlack.copy(alpha = 0.5f)) }
        } else null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        isError = isError,
        supportingText = if (supportingText != null) {
            { Text(supportingText, color = Color.Red) }
        } else null,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BorderBlack,
            unfocusedBorderColor = BorderBlack,
            cursorColor = Primary,
            focusedLabelColor = Primary,
            unfocusedLabelColor = TextBlack,
            focusedTextColor = TextBlack,
            unfocusedTextColor = TextBlack,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface
        ),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}
