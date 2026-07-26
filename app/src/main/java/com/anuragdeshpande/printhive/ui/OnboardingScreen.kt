package com.anuragdeshpande.printhive.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anuragdeshpande.printhive.ui.components.FleetConvergenceAnimation
import com.anuragdeshpande.printhive.ui.components.PrintHiveHeaderLogo
import com.anuragdeshpande.printhive.ui.components.ServerScanAnimation
import com.anuragdeshpande.printhive.ui.components.SpoolScanAnimation
import com.anuragdeshpande.printhive.ui.components.SpoolTagMarquee
import com.anuragdeshpande.printhive.ui.components.ThermalPrinterAnimation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val title: String,
    val description: String,
)

private val slides = listOf(
OnboardingSlide(
    title = "Welcome to PrintHive",
    description = "Group printers from different brands and manufacturers under one platform and free your workflows from cloud dependencies.",
),
OnboardingSlide(
    title = "Fleet Control",
    description = "Monitor and control your entire 3D printer fleet from one glanceable dashboard.",
),
OnboardingSlide(
    title = "Spool QR Scanning",
    description = "Scan spool QR codes to instantly assign filament to AMS slots or storage. Compatible with Bambu Lab RFID, Spoolman, and PrintHive QR tags.",
),
OnboardingSlide(
    title = "Thermal Label Printing",
    description = "Print 30mm circular spool labels wirelessly via Bluetooth to a Phomemo printer with top-margin calibration.",
),
OnboardingSlide(
    title = "You're Ready!",
    description = "Your PrintHive setup is ready to use.",
),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToServerQr: () -> Unit,
    isServerConnected: Boolean = false,
    serverUrl: String? = null,
    onFinish: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    var welcomeAnimationStarted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0 && !welcomeAnimationStarted) {
            delay(250)
            welcomeAnimationStarted = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            val slide = slides[page]
            if (page == 0) {
                WelcomeOnboardingSlide(
                    title = slide.title,
                    description = slide.description,
                    animationStarted = welcomeAnimationStarted,
                )
            } else if (page == slides.size - 1) {
                ReadyOnboardingSlide(
                    title = if (isServerConnected) slide.title else "One more thing...",
                    description = slide.description,
                    isServerConnected = isServerConnected,
                    serverUrl = serverUrl,
                    onFinish = onFinish,
                )
            } else {
                FeatureOnboardingSlide(
                    page = page,
                    title = slide.title,
                    description = slide.description,
                    showServerActions = false,
                    onNavigateToServerQr = onNavigateToServerQr,
                    onSkipServerSetup = {
                        scope.launch { pagerState.animateScrollToPage(3) }
                    },
                )
            }
        }

        // Pager Indicator Dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(slides.size) { index ->
                val selected = pagerState.currentPage == index
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp),
                    shape = MaterialTheme.shapes.small,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    },
                ) {}
            }
        }

        // Bottom Navigation Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(88.dp))
            }

            if (pagerState.currentPage < slides.size - 1) {
                Button(onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }) {
                    Text("Next")
                }
            } else {
                Spacer(modifier = Modifier.width(88.dp))
            }
        }
    }
}

@Composable
private fun WelcomeOnboardingSlide(
    title: String,
    description: String,
    animationStarted: Boolean,
) {
    val uriHandler = LocalUriHandler.current
    val logoTopPadding by animateDpAsState(
        targetValue = if (animationStarted) 28.dp else 220.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 260f),
        label = "welcome-logo-top-padding",
    )
    val logoWidth by animateDpAsState(
        targetValue = if (animationStarted) 212.dp else 290.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 260f),
        label = "welcome-logo-width",
    )
    val messageAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "welcome-message-alpha",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            PrintHiveHeaderLogo(
                modifier = Modifier.padding(top = logoTopPadding),
                width = logoWidth,
            )

            Column(
                modifier = Modifier.alpha(messageAlpha),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "This app requires a self-hosted PrintHive server.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "See the official PrintHive server project: github.com/anuragdeshpande/printhive",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(onClick = { uriHandler.openUri("https://github.com/anuragdeshpande/printhive") }) {
                            Text("Open GitHub Repository")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadyOnboardingSlide(
    title: String,
    description: String,
    isServerConnected: Boolean,
    serverUrl: String?,
    onFinish: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PrintHiveHeaderLogo(width = 180.dp)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        if (isServerConnected) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth(0.9f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Server configuration available",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(52.dp),
        ) {
            Text("Get Started", fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FeatureOnboardingSlide(
    page: Int,
    title: String,
    description: String,
    showServerActions: Boolean,
    onNavigateToServerQr: () -> Unit,
    onSkipServerSetup: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PrintHiveHeaderLogo(width = 152.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Animation Header based on onboarding page
        when (page) {
            1 -> FleetConvergenceAnimation()
            2 -> SpoolScanAnimation()
            3 -> ThermalPrinterAnimation()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        // Marquee badge strip for Spool QR page
        if (page == 2) {
            Spacer(modifier = Modifier.height(12.dp))
            SpoolTagMarquee()
        }
    }
}
