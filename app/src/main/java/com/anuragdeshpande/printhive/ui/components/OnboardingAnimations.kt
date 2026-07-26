package com.anuragdeshpande.printhive.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anuragdeshpande.printhive.R
import kotlinx.coroutines.delay

private val BrandAmber = Color(0xFFF59E0B)
private val BrandAmberGlow = Color(0x33F59E0B)
private val ElectricGreen = Color(0xFF10B981)
private val CyberBlue = Color(0xFF3B82F6)

@Composable
fun FleetConvergenceAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "fleet-transition")
    val pulseProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulse-progress",
    )

    Box(
        modifier = modifier
            .height(220.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, BrandAmberGlow, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // 4 Radial network lines to corners
            val cornerOffsets = listOf(
                Offset(size.width * 0.22f, size.height * 0.22f), // Top-Left: Klipper
                Offset(size.width * 0.78f, size.height * 0.22f), // Top-Right: Bambu Lab
                Offset(size.width * 0.22f, size.height * 0.78f), // Bottom-Left: Elegoo
                Offset(size.width * 0.78f, size.height * 0.78f), // Bottom-Right: Prusa
            )

            cornerOffsets.forEach { nodePos ->
                drawLine(
                    color = Color(0xFF334155),
                    start = nodePos,
                    end = center,
                    strokeWidth = 3.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                )

                // Energy packet moving along line to center
                val packetPos = Offset(
                    nodePos.x + (center.x - nodePos.x) * pulseProgress,
                    nodePos.y + (center.y - nodePos.y) * pulseProgress,
                )
                drawCircle(
                    color = BrandAmber,
                    radius = 7.5f,
                    center = packetPos,
                )
            }

            // Central PrintHive Pulse Halo
            val haloRadius = 45f + (pulseProgress * 30f)
            val haloAlpha = (1f - pulseProgress).coerceIn(0f, 1f)
            drawCircle(
                color = ElectricGreen.copy(alpha = haloAlpha * 0.5f),
                radius = haloRadius,
                center = center,
            )

            // Central Core Circle
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 42f,
                center = center,
            )
            drawCircle(
                color = ElectricGreen,
                radius = 42f,
                center = center,
                style = Stroke(width = 4f),
            )
        }

        // Top-Left: Klipper
        VendorBadgeItem(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp),
            name = "Klipper",
            brandColor = Color(0xFFD97706),
        )

        // Top-Right: Bambu Lab
        VendorBadgeItem(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp),
            name = "Bambu Lab",
            brandColor = Color(0xFF00AE42),
        )

        // Bottom-Left: Elegoo
        VendorBadgeItem(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp, start = 16.dp),
            name = "Elegoo",
            brandColor = Color(0xFF00A3E0),
        )

        // Bottom-Right: Prusa Research
        VendorBadgeItem(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp),
            name = "Prusa Research",
            brandColor = Color(0xFFFA6400),
        )

        // Central PrintHive Checkmark Core
        Surface(
            shape = CircleShape,
            color = ElectricGreen,
            modifier = Modifier.size(42.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
        }
    }
}

@Composable
private fun VendorBadgeItem(
    name: String,
    brandColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, brandColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = brandColor,
                modifier = Modifier.size(16.dp),
            ) {}
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun ServerScanAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "server-transition")
    val laserProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "laser-progress",
    )

    Box(
        modifier = modifier
            .height(210.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, CyberBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left Side Smartphone Frame containing perfectly centered QR code (Black dots on white bg)
            Box(
                modifier = Modifier
                    .width(135.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E293B))
                    .border(3.dp, Color(0xFF475569), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                // QR Code Image (Black dots on White bg) centered perfectly inside screen
                Image(
                    painter = painterResource(R.drawable.printhive_stylized_qr_light),
                    contentDescription = "PrintHive QR Code",
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit,
                )

                // Laser scan line inside phone screen
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val laserY = size.height * laserProgress
                    drawLine(
                        color = BrandAmber,
                        start = Offset(8f, laserY),
                        end = Offset(size.width - 8f, laserY),
                        strokeWidth = 4.5f,
                        cap = StrokeCap.Round,
                    )
                }
            }

            // Server Tower Vector (Right Side)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(170.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(2.5.dp, Color(0xFF475569), RoundedCornerShape(12.dp)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    repeat(3) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = ElectricGreen,
                                modifier = Modifier.size(7.dp),
                            ) {}
                        }
                    }
                }

                // Connection Checkmark Badge (unclipped inside server bounds)
                Surface(
                    shape = CircleShape,
                    color = ElectricGreen,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 10.dp, end = 10.dp)
                        .size(28.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpoolScanAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "spool-transition")
    val laserProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "spool-laser",
    )

    Box(
        modifier = modifier
            .height(210.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, ElectricGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        // Centered Camera Viewfinder Frame with Stylized QR Code (Black dots on white bg)
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center,
        ) {
            // QR Code (Black dots on white background) centered perfectly
            Image(
                painter = painterResource(R.drawable.printhive_stylized_qr_light),
                contentDescription = "PrintHive QR Code",
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit,
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val vfSize = size.width
                val bracketPath = Path().apply {
                    moveTo(0f, 22f)
                    lineTo(0f, 0f)
                    lineTo(22f, 0f)

                    moveTo(vfSize - 22f, 0f)
                    lineTo(vfSize, 0f)
                    lineTo(vfSize, 22f)

                    moveTo(0f, vfSize - 22f)
                    lineTo(0f, vfSize)
                    lineTo(22f, vfSize)

                    moveTo(vfSize - 22f, vfSize)
                    lineTo(vfSize, vfSize)
                    lineTo(vfSize, vfSize - 22f)
                }
                drawPath(
                    path = bracketPath,
                    color = ElectricGreen,
                    style = Stroke(width = 5f, cap = StrokeCap.Round),
                )

                // Laser Beam
                val scanY = size.height * laserProgress
                drawLine(
                    color = ElectricGreen,
                    start = Offset(0f, scanY),
                    end = Offset(size.width, scanY),
                    strokeWidth = 4.5f,
                )
            }
        }
    }
}

@Composable
fun SpoolTagMarquee(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            scrollState.animateScrollTo(
                value = scrollState.maxValue,
                animationSpec = tween(durationMillis = 14000, easing = LinearEasing),
            )
            scrollState.scrollTo(0)
            delay(100)
        }
    }

    val tagBadges = listOf(
        "Bambu RFID",
        "Spoolman QR",
        "PrintHive JSON",
        "Bambuddy QR",
        "Polymaker Spools",
        "eSUN Tag",
        "Sunlu RFID",
        "Hatchbox QR",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tagBadges.forEach { badge ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricGreen.copy(alpha = 0.6f)),
            ) {
                Text(
                    text = badge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun ThermalPrinterAnimation(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "printer-transition")
    val feedProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "label-feed",
    )

    Box(
        modifier = modifier
            .height(210.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, Color(0xFFA855F7).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Paper Exit Exit Slot Clipping Box (Paper emerges strictly out of slot)
            Box(
                modifier = Modifier
                    .width(135.dp)
                    .height(95.dp)
                    .clipToBounds(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                val offsetY = (95 * (1f - feedProgress)).dp
                Surface(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .size(90.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.printhive_stylized_qr_light),
                            contentDescription = "Printed 30mm PrintHive Label",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }

            // Phomemo Printer Body Frame
            Box(
                modifier = Modifier
                    .width(190.dp)
                    .height(75.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(3.5.dp, Color(0xFFA855F7), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.TopCenter,
            ) {
                // Exit Slot Line Bar
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .width(140.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F172A)),
                )

                // Power Light Indicator
                Surface(
                    shape = CircleShape,
                    color = ElectricGreen,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 12.dp)
                        .size(8.dp),
                ) {}
            }
        }
    }
}
