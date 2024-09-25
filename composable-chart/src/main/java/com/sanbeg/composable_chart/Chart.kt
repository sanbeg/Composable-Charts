package com.sanbeg.composable_chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class ComposableChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
)

@Composable
fun Chart(
    minX: Float = 0f,
    maxX: Float = 0f,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) {
    ComposableChartScope(minX, maxX).content()
}
