package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart_data.asDataSet

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
    Spacer(modifier.asPlot().fillMaxSize().drawBehind {
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

@Preview(showBackground = true)
@Composable
private fun PreviewChart() {
    Chart(maxX = 100f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

            drawEach(
                listOf(
                    Offset(25f, 25f),
                    Offset(0f, 0f),
                    Offset(100f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 3.dp.toPx(), it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartFlip() {
    Chart(maxX = 100f, dataInset = 4.dp, modifier = Modifier.size(100.dp)) {
        Scale(minY = 100f, maxY = 0f) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(
                listOf(
                    Offset(25f, 25f),
                    Offset(0f, 0f),
                    Offset(100f, 100f),
                    ).asDataSet()
            ) {
                drawCircle(Color.Blue, 4.dp.toPx(), it)
            }
        }
    }
}