package com.example.myapplicationds.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationds.ui.theme.*

/**
 * Premium Black Glassmorphism Card Container (24dp rounded corners, thin 8% white glass border)
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color(0x1F141418), // 10-15% transparency
    borderColor: Color = Color.White.copy(alpha = 0.08f),
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = modifier
        .shadow(
            elevation = 6.dp,
            shape = shape,
            ambientColor = Color.Black,
            spotColor = Color.Black.copy(alpha = 0.4f)
        )
        .clip(shape)
        .background(backgroundColor)
        .border(BorderStroke(borderWidth, borderColor), shape)
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )

    Column(
        modifier = baseModifier.padding(contentPadding),
        content = content
    )
}

/**
 * Glass Search Bar with 56dp height, 18dp rounded corners, brighter border & crisp placeholder contrast
 */
@Composable
fun GlassSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "Search bills...",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .border(
                BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        color = Color(0x1A1E1E24)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = PrimaryBlueLight,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholderText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                }
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (value.isNotEmpty()) {
                IconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Search",
                        tint = TextSecondaryDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Redesigned Pill-Style Tab Component
 * Smooth selection animation, thin indicator, circular count badges (hidden if count == 0).
 */
@Composable
fun GlassPillTabs(
    tabs: List<Pair<String, Int>>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0x1F121216))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(26.dp)),
        color = Color(0x18121218),
        shape = RoundedCornerShape(26.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, (label, count) ->
                val isSelected = selectedTabIndex == index

                val targetBg = if (isSelected) Color(0x333B82F6) else Color.Transparent
                val animBackground by animateColorAsState(targetBg, animationSpec = tween(250), label = "tabBg")

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(animBackground)
                        .then(
                            if (isSelected) Modifier.border(BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.5f)), RoundedCornerShape(22.dp)) else Modifier
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextPrimaryDark else TextSecondaryDark,
                            fontSize = 14.sp
                        )

                        // Circular count badge - hidden if count == 0
                        if (count > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) PrimaryBlue else Color(0x26FFFFFF)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = count.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sleek Dark Mark as Paid Button (matching reference screenshot)
 */
@Composable
fun GlassMarkPaidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Mark as Paid"
) {
    Surface(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = Color(0xFF0D0D12),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp
            )
        }
    }
}

/**
 * Circular Glass Icon Button (48dp minimum touch target for accessibility & header buttons)
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextSecondaryDark,
    containerColor: Color = Color(0x1F22222A)
) {
    Box(
        modifier = modifier
            .size(48.dp) // 48dp accessibility target
            .clip(CircleShape)
            .background(containerColor)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}

