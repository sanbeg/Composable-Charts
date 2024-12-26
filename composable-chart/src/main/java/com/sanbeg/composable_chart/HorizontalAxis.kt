package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.Point
import kotlin.math.max
import kotlin.math.min

class HorizontalAxisScope internal constructor(
    private val chartScope: ComposableChartScope,
    @PublishedApi
    internal val drawScope: DrawScope,
    ) {

    val minVal by chartScope::minX
    val maxVal by chartScope::maxX

    private val scale = (drawScope.size.width - chartScope.dataInset * 2) / (maxVal - minVal)

    fun scale(x: Float): Float = (x -minVal)* scale + chartScope.dataInset

}

@Composable
fun ComposableChartScope.HorizontalAxis(
    modifier: Modifier,
    edge: Edge = Edge.BOTTOM,
    content: HorizontalAxisScope.() -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .asAxis(edge)
            .drawBehind {
                HorizontalAxisScope(
                    this@HorizontalAxis,
                    this
                ).content()
            }
    )
}

inline fun HorizontalAxisScope.drawAt(x: Float, draw: DrawScope.() -> Unit) {
    drawScope.translate(scale(x)) {
        draw()
    }
}

fun HorizontalAxisScope.drawTics(spacing: Float) {
    var x = min(minVal, maxVal)
    while (x <= max(minVal, maxVal)) {
        drawAt(x) {
            drawLine(Color.Black, Offset.Zero, Offset(0f, size.height))
        }
        x += spacing
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxis() {
    Chart(maxX = 100f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            drawEach(
                listOf(
                    Point(25f, 25f),
                    Point(0f, 0f),
                    Point(100f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 3.dp.toPx(), it)
            }
        }

        HorizontalAxis(Modifier.height(8.dp)) {
            drawTics(10f)
        }
        HorizontalAxis(Modifier.height(4.dp).background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxisShift() {
    Chart(minX = 100f, maxX = 200f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            drawEach(
                listOf(
                    Point(125f, 25f),
                    Point(100f, 0f),
                    Point(200f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 3.dp.toPx(), it)
            }
        }

        HorizontalAxis(Modifier.height(8.dp)) {
            drawTics(10f)
        }
        HorizontalAxis(Modifier.height(4.dp).background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxisFlip() {
    Chart(minX = 100f, maxX = 0f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            drawEach(
                listOf(
                    Point(25f, 25f),
                    Point(0f, 0f),
                    Point(100f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 3.dp.toPx(), it)
            }
        }

        HorizontalAxis(Modifier.height(8.dp)) {
            drawTics(10f)
        }
        HorizontalAxis(Modifier.height(4.dp).background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}