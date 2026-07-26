package com.anuragdeshpande.printhive.ui.theme

import android.animation.ValueAnimator
import android.content.Context
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PrintHiveSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
)

@Immutable
data class PrintHiveRadii(
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 28.dp,
)

@Immutable
data class PrintHiveElevation(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
)

@Immutable
object PrintHiveMotionSpec {
    /** Card expansion/collapse spring animation */
    val drawerExpandSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = 380f,
    )

    /** Micro-updates for progress bars and telemetry gauges */
    val progressTween: AnimationSpec<Float> = tween(
        durationMillis = 400,
        easing = FastOutSlowInEasing,
    )

    /** Button press scale feedback */
    val buttonPressSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.60f,
        stiffness = 600f,
    )

    /** Status badge fade/scale transition */
    val statusChipTween: AnimationSpec<Float> = tween(
        durationMillis = 250,
        easing = FastOutSlowInEasing,
    )

    /** Platform-safe reduced motion check using ValueAnimator */
    fun isMotionEnabled(context: Context, reduceMotionUserSetting: Boolean): Boolean {
        val areAnimatorsEnabled = ValueAnimator.areAnimatorsEnabled()
        return areAnimatorsEnabled && !reduceMotionUserSetting
    }
}

val LocalPrintHiveSpacing = staticCompositionLocalOf { PrintHiveSpacing() }
val LocalPrintHiveRadii = staticCompositionLocalOf { PrintHiveRadii() }
val LocalPrintHiveElevation = staticCompositionLocalOf { PrintHiveElevation() }
