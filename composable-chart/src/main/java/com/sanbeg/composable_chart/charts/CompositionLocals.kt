package com.sanbeg.composable_chart.charts

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

val LocalFunctionResolution = compositionLocalOf { 0.5.dp }
val LocalFunctionResolutionPx = compositionLocalWithComputedDefaultOf {
    with(LocalDensity.currentValue) {
        LocalFunctionResolution.currentValue.toPx()
    }
}
