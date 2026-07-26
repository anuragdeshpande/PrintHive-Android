package com.anuragdeshpande.printhive.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anuragdeshpande.printhive.R

@Composable
fun PrintHiveHeaderLogo(
    modifier: Modifier = Modifier,
    width: Dp = 148.dp,
    showGlow: Boolean = true,
) {
    // Hexagon 3D cube center is at exact 15.76% of the total logo width (150px / 952px image)
    val markCenterX = width * 0.1576f
    val glowDiameter = width * 0.44f

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        if (showGlow) {
            // Ambient orange radial glow centered DEAD CENTER behind the 3D hexagon logo icon
            Box(
                modifier = Modifier
                    .size(glowDiameter)
                    .offset(x = markCenterX - (glowDiameter / 2f))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF59E0B).copy(alpha = 0.65f),
                                Color(0xFFEA580C).copy(alpha = 0.28f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
        }
        Image(
            painter = painterResource(R.drawable.printhive_header_logo),
            contentDescription = "PrintHive",
            modifier = Modifier.width(width),
            contentScale = ContentScale.Fit,
        )
    }
}
