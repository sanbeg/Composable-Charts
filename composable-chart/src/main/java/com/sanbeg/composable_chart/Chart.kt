package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.drawEach

class ComposableChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
    ) {
    internal var dataInset: Float = 0f
}

@Composable
fun Chart(
    minX: Float = 0f,
    maxX: Float = 1f,
    dataInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) {
    Box(modifier) {
        ComposableChartScope(minX, maxX).also {
            it.dataInset = dataInset.value
        }.content()
    }
}


@Composable
fun Chart2(
    minX: Float = 0f,
    maxX: Float = 1f,
    dataInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) {
    val layoutContent: @Composable () -> Unit = {
        ComposableChartScope(minX, maxX).also {
            it.dataInset = dataInset.value
        }.content()
    }
    Layout(layoutContent, modifier) {measurables, constraints ->
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            measurable.measure(contentConstraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                val position = Alignment.BottomStart.align(
                    IntSize(placeable.width, placeable.height),
                    IntSize(constraints.maxWidth, constraints.maxHeight),
                    LayoutDirection.Ltr
                )
                //placeable.placeRelative(x = 0, y = 0)
                placeable.place(position)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChart() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

            drawEach(
                listOf(
                    Offset(25f, 25f),
                    Offset(0f, 0f),
                    Offset(100f, 100f),
                )
            ) {
                drawCircle(Color.Blue, 6.dp.value, it)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewChart2() {
    Chart2(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

            drawEach(
                listOf(
                    Offset(25f, 25f),
                    Offset(0f, 0f),
                    Offset(100f, 100f),
                )
            ) {
                drawCircle(Color.Blue, 6.dp.value, it)
            }
        }
        Spacer(Modifier.fillMaxWidth().height(10.dp).background(Color.Red))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartFlip() {
    Chart(maxX = 100f, modifier = Modifier.size(100.dp)) {
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