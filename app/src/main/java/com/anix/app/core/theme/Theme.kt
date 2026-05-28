package com.anix.app.core.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Background = Color(0xFFFFF8F0)
val Surface = Color(0xFFFFFFFF)
val Primary = Color(0xFF3355FF)
val Secondary = Color(0xFFFF3399)
val AccentLime = Color(0xFF33FF55)
val AccentOrange = Color(0xFFFF6633)
val TextBlack = Color(0xFF1A1A1A)
val BorderBlack = Color(0xFF000000)
val ShadowColor = Color(0x40000000)
val Success = Color(0xFF33FF55)
val Error = Color(0xFFFF3333)
val Warning = Color(0xFFFFCC00)

val PurpleDark = Color(0xFF1A0033)
val BlueDark = Color(0xFF001133)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    background = Background,
    onBackground = TextBlack,
    surface = Surface,
    onSurface = TextBlack,
    error = Error,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    background = PurpleDark,
    onBackground = Color(0xFFE0E0E0),
    surface = BlueDark,
    onSurface = Color(0xFFE0E0E0),
    error = Error,
    onError = Color.White,
)

@Composable
fun AnixTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        fontFamily = FontFamily.SansSerif,
        color = TextBlack
    ),
)

val Shapes = androidx.compose.material3.Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

val NeoBorder = BorderStroke(2.dp, BorderBlack)
val NeoShape = RoundedCornerShape(8.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoBrutalismCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val cardModifier = modifier
        .border(NeoBorder, shape)

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick ?: { }
    ) {
        Box(
            modifier = Modifier
                .background(Surface)
                .then(
                    if (onClick == null) Modifier else Modifier
                )
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Surface)
            )
            content()
        }
    }
}

@Composable
fun NeoBrutalismButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Primary,
    textColor: Color = Color.White,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(8.dp)
    Button(
        onClick = onClick,
        modifier = modifier
            .border(NeoBorder, shape),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f),
            disabledContentColor = textColor.copy(alpha = 0.5f)
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NeoBrutalismTextField(
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
    supportingText: String? = null
) {
    val shape = RoundedCornerShape(8.dp)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .border(NeoBorder, shape),
        shape = shape,
        label = if (label.isNotEmpty()) {
            { Text(label, fontWeight = FontWeight.Bold) }
        } else null,
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder) }
        } else null,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        isError = isError,
        supportingText = if (supportingText != null) {
            { Text(supportingText) }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BorderBlack,
            unfocusedBorderColor = BorderBlack,
            cursorColor = Primary,
            focusedLabelColor = Primary,
            unfocusedLabelColor = TextBlack,
            focusedTextColor = TextBlack,
            unfocusedTextColor = TextBlack,
            errorBorderColor = Error,
            errorLabelColor = Error,
            errorCursorColor = Error,
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            errorContainerColor = Surface
        ),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}
