package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope

class ComposableChartScaleScope internal constructor(
    private val matrix: Matrix,
    internal val drawScope: DrawScope,
) {
    internal fun scale(offset: Offset) = if (offset.isSpecified) {
        matrix.map(offset)
    } else {
        offset
    }
}

/**
 * A composable which provides a scaling for its content.  The content is invoked in a scope which
 * provides functionality to scale [Offset]s from real-world units to pixels.
 *
 * @param[minY] The Y value which will scale to the bottom of the chart.
 * @param[maxY] The Y value which will scale to the top of the chart.
 *
 * Note than [minY] and [maxY] should not be the same.  If [minY] < [maxY], then increasing values
 * will be drawn closer to the top.  Swapping [minY] and [maxY] would cause the graph to be inverted.
 *
 * @param[modifier] The modifier to apply to the scale.
 * @param[content] The content of the Scale.
 *
 */
@Composable
fun ComposableChartScope.Scale(
    minY: Float = 0f,
    maxY: Float = 1f,
    modifier: Modifier = Modifier,
    content: ComposableChartScaleScope.() -> Unit
    ) {
    Spacer(modifier.fillMaxSize().drawBehind {
        Matrix().apply {
            translate(x = dataInset, y = dataInset)
            val di2 = dataInset * 2
            scale(
                x = (size.width - di2) / (maxX - minX),
                y = (size.height - di2) / -(maxY - minY),
            )
            translate(
                y = -maxY
            )
        }.let { ComposableChartScaleScope(it, this) }
            .content()
    })
}