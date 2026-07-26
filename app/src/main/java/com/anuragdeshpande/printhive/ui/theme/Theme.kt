package com.anuragdeshpande.printhive.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val PrintHiveExpressiveDarkScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Slate950,
    primaryContainer = AmberContainer,
    onPrimaryContainer = OnAmberContainer,
    secondary = AmberSecondary,
    onSecondary = Slate950,
    secondaryContainer = Slate800,
    onSecondaryContainer = TextPrimary,
    tertiary = MintEmerald,
    onTertiary = Slate950,
    tertiaryContainer = MintEmeraldContainer,
    onTertiaryContainer = OnMintEmeraldContainer,
    background = Slate950,
    onBackground = TextPrimary,
    surface = Slate900,
    onSurface = TextPrimary,
    surfaceVariant = Slate850,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = Slate900,
    surfaceContainerLow = Slate850,
    surfaceContainer = Slate800,
    surfaceContainerHigh = Slate700,
    surfaceContainerHighest = Slate600,
    error = ErrorRose,
    onError = TextPrimary,
    errorContainer = ErrorRoseContainer,
    onErrorContainer = OnErrorRoseContainer,
)

private val PrintHiveExpressiveLightScheme = lightColorScheme(
    primary = AmberPrimaryDim,
    onPrimary = TextPrimary,
    primaryContainer = OnAmberContainer,
    onPrimaryContainer = AmberContainer,
    secondary = Slate800,
    onSecondary = TextPrimary,
    background = TextPrimary,
    onBackground = Slate950,
    surface = Color(0xFFF8FAFC),
    onSurface = Slate950,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Slate800,
    error = ErrorRose,
    onError = TextPrimary,
)

@Composable
fun PrintHiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> PrintHiveExpressiveDarkScheme
        else -> PrintHiveExpressiveLightScheme
    }

    CompositionLocalProvider(
        LocalPrintHiveSpacing provides PrintHiveSpacing(),
        LocalPrintHiveRadii provides PrintHiveRadii(),
        LocalPrintHiveElevation provides PrintHiveElevation(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
