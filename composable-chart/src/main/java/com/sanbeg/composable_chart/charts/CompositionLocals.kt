package com.sanbeg.composable_chart.charts

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.compositionLocalWithComputedDefaultOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Discrete functions are plotted by evaluating the function at points within the Plot's X range.
 * This specifies the resolution to evaluate the function in Dp.  Smaller values will render a smoother function,
 * while larger values will be faster to compute.
 */
val LocalFunctionResolution = compositionLocalOf { 0.5.dp }

/**
* Discrete functions are plotted by evaluating the function at points within the Plot's X range.
* This specifies the resolution to evaluate the function in pixels.  Its default value is computed by
 * converting [LocalFunctionResolution] with the current screen density.
*/
val LocalFunctionResolutionPx = compositionLocalWithComputedDefaultOf {
    with(LocalDensity.currentValue) {
        LocalFunctionResolution.currentValue.toPx()
    }
}
