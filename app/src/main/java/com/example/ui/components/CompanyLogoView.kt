package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.entity.BrandConfig

@Composable
fun CompanyLogoView(
    brandConfig: BrandConfig?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isFavicon: Boolean = false
) {
    val url = if (isFavicon) brandConfig?.faviconUrl else brandConfig?.logoUrl
    val symbol = if (isFavicon) "SHIELD" else (brandConfig?.logoSymbol ?: "TRENDING")
    val emoji = if (isFavicon) (brandConfig?.customFaviconEmoji ?: "🛡️") else (brandConfig?.customEmojiLogo ?: "📈")

    val containerColor = if (isFavicon) Color(0xFF00E676).copy(alpha = 0.25f) else Color(0xFF00E676)
    val tintColor = if (isFavicon) Color(0xFF00E676) else Color.Black

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = if (isFavicon) "Favicon" else "Logo",
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
            )
        } else if (symbol == "EMOJI") {
            Text(
                text = emoji,
                fontSize = (size.value * 0.5f).sp
            )
        } else {
            val iconVector: ImageVector = when (symbol) {
                "SHIELD" -> Icons.Default.Security
                "DIAMOND" -> Icons.Default.Star
                "ROCKET" -> Icons.Default.RocketLaunch
                "BUSINESS" -> Icons.Default.Business
                "STAR" -> Icons.Default.Star
                "GLOBE" -> Icons.Default.Public
                "WORK" -> Icons.Default.Work
                else -> Icons.AutoMirrored.Filled.TrendingUp
            }
            Icon(
                imageVector = iconVector,
                contentDescription = if (isFavicon) "Favicon" else "Logo",
                tint = tintColor,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}
