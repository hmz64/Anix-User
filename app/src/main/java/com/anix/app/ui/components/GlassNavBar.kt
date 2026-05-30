package com.anix.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.util.liquidGlass
import kotlin.math.roundToInt

data class NavItem(
    val route: String,
    val icon: ImageVector,
    val iconSelected: ImageVector,
    val label: String,
)

@Composable
fun GlassNavBar(
    items: List<NavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = items.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    val tabPositions = remember { mutableStateListOf<Float>().apply { repeat(items.size) { add(0f) } } }
    val tabWidths = remember { mutableStateListOf<Float>().apply { repeat(items.size) { add(0f) } } }

    val indicatorX by animateFloatAsState(
        targetValue = tabPositions.getOrElse(selectedIndex) { 0f },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "indicator_x"
    )

    val indicatorWidth by animateFloatAsState(
        targetValue = tabWidths.getOrElse(selectedIndex) { 0f },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "indicator_width"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .liquidGlass(shape = RoundedCornerShape(50.dp), blurRadius = 32f, alpha = 0.13f, showGlow = true)
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(indicatorX.roundToInt(), 0) }
                    .width(with(LocalDensity.current) { indicatorWidth.toDp() })
                    .fillMaxHeight()
                    .padding(vertical = 10.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AccentBlue.copy(alpha = 0.35f), AccentBlue.copy(alpha = 0.20f))
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex

                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "icon_scale_$index"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) AccentBlue else TextMuted,
                        animationSpec = tween(durationMillis = 250),
                        label = "icon_color_$index"
                    )
                    val labelAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0f,
                        animationSpec = tween(durationMillis = 200),
                        label = "label_alpha_$index"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned { coords ->
                                tabPositions[index] = coords.positionInParent().x
                                tabWidths[index] = coords.size.width.toFloat()
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onItemClick(item.route) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.iconSelected else item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp).scale(iconScale)
                        )
                        if (labelAlpha > 0f) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.label,
                                color = AccentBlue.copy(alpha = labelAlpha),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
