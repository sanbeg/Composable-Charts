package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.Spacer
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

class ComposableChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
)

class ComposableChartScaleScope internal constructor(
    internal val matrix: Matrix,
    internal val drawScope: DrawScope,
)

@Composable
fun Chart(
    minX: Float = 0f,
    maxX: Float = 1f,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) {
    ComposableChartScope(minX, maxX).content()
}

@Composable
fun ComposableChartScope.Scale(
    minY: Float = 0f,
    maxY: Float = 1f,
    modifier: Modifier = Modifier.size(100.dp),
    content: ComposableChartScaleScope.() -> Unit
    ) {
    Spacer(modifier.drawBehind {
        Matrix().apply {
            scale(
                x = size.width / (maxX - minX),
                y = size.height / -(maxY - minY),
            )
            translate(
                y = -maxY
            )
        }.let { ComposableChartScaleScope(it, this) }
            .content()
    })
}

fun ComposableChartScaleScope.drawEach(
    offsets: List<Offset>,
    content: DrawScope.(offset: Offset) -> Unit
) {
    offsets
        .filter(Offset::isSpecified)
        .map(matrix::map)
        .forEach{
            drawScope.content(it)
        }

}

@Preview(showBackground = true)
@Composable
fun previewChart() {
    Chart(maxX = 100f) {
        Scale(maxY = 100f) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(
                listOf(Offset(25f, 25f))
            ) {
                drawCircle(Color.Blue, 4.dp.value, it)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewChartFlip() {
    Chart(maxX = 100f) {
        Scale(minY = 100f, maxY = 0f) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(
                listOf(Offset(25f, 25f))
            ) {
                drawCircle(Color.Blue, 4.dp.value, it)
            }
        }
    }
}