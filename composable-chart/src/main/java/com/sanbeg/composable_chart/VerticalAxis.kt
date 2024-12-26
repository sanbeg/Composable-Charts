package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

class VerticalAxisScope internal constructor(
    private val chartScope: ComposableChartScope,
    @PublishedApi
    internal val drawScope: DrawScope,
    internal val minVal: Float,
    internal val maxVal: Float,
) {
    private val scale = (drawScope.size.height - chartScope.dataInset * 2) / -(maxVal - minVal)

    fun scale(y: Float): Float = (y - maxVal) * scale + chartScope.dataInset
}

@Composable
fun ComposableChartScope.VerticalAxis(
    minY: Float,
    maxY: Float,
    modifier: Modifier = Modifier,
    edge: Edge = Edge.LEFT,
    content: VerticalAxisScope.() -> Unit
) {
    Box(
        modifier
            .fillMaxHeight()
            .asAxis(edge)
            .drawBehind {
                VerticalAxisScope(
                    this@VerticalAxis,
                    this,
                    minY,
                    maxY,
                ).content()
            }
    )
}

inline fun VerticalAxisScope.drawAt(y: Float, draw: DrawScope.(y: Float) -> Unit) {
    drawScope.translate(top=scale(y)) {
        draw(y)
    }
}

fun VerticalAxisScope.drawTics(spacing: Float) {
    var y = min(minVal, maxVal)
    while (y <= max(minVal, maxVal)) {
        drawAt(y) {
            drawLine(Color.Black, Offset.Zero, Offset(size.width, 0f))
        }
        y += spacing
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartVerticalAxis() {
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

        VerticalAxis(0f, 100f, Modifier.width(8.dp)) {
            drawTics(10f)
        }
        VerticalAxis(0f, 100f,
            Modifier
                .width(4.dp)
                .background(Color.Cyan), edge = Edge.RIGHT) {
            drawTics(15f)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartVerticalAxisShift() {
    Chart(maxX = 100f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(minY = 100f, maxY = 200f, modifier = Modifier) {
            drawEach(
                listOf(
                    Point(25f, 125f),
                    Point(0f, 100f),
                    Point(100f, 200f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 3.dp.toPx(), it)
            }
        }

        VerticalAxis(100f, 200f, Modifier.width(8.dp)) {
            drawTics(10f)
        }
        VerticalAxis(100f, 200f,
            Modifier
                .width(4.dp)
                .background(Color.Cyan), edge = Edge.RIGHT) {
            drawTics(15f)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartVerticalAxisFlip() {
    Chart(maxX = 100f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(minY = 100f, maxY = 0f, modifier = Modifier) {
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

        VerticalAxis(100f, 0f, Modifier.width(8.dp)) {
            drawTics(10f)
        }
        VerticalAxis(100f, 0f,
            Modifier
                .width(4.dp)
                .background(Color.Cyan), edge = Edge.RIGHT) {
            drawTics(15f)
        }
    }
}