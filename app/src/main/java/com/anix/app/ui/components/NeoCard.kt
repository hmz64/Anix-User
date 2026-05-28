package com.anix.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = Surface,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Card(
        modifier = modifier
            .shadow(4.dp, shape, ambientColor = BorderBlack, spotColor = BorderBlack),
        shape = shape,
        border = BorderStroke(2.dp, BorderBlack),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick ?: { }
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}
