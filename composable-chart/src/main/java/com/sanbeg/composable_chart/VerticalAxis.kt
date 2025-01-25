package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.ModifierLocalDataInset
import com.sanbeg.composable_chart.core.ModifierLocalRangeY
import com.sanbeg.composable_chart.core.plotInset
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.ChartRange
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.geometry.max
import com.sanbeg.composable_chart_data.geometry.min

class VerticalAxisScope internal constructor(
    @PublishedApi
    internal val drawScope: DrawScope,
    internal val yRange: ChartRange,
    private val dataInset: Float
) {
    private val scale = (drawScope.size.height - dataInset * 2) / -(yRange.end - yRange.start)

    fun scale(y: Float): Float = (y - yRange.end) * scale + dataInset
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.VerticalAxis(
    modifier: Modifier = Modifier,
    edge: Edge = Edge.LEFT,
    content: VerticalAxisScope.() -> Unit
) {
    var yrange = ChartRange.Normal
    var inset = 0.dp
    Box(
        modifier
            .modifierLocalConsumer {
                yrange = ModifierLocalRangeY.current
                inset = ModifierLocalDataInset.current
            }
            .fillMaxHeight()
            .asAxis(edge)
            .drawBehind {
                VerticalAxisScope(
                    this,
                    yrange,
                    inset.toPx(),
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
    var y = yRange.min
    while (y <= yRange.max) {
        drawAt(y) {
            drawLine(Color.Black, Offset.Zero, Offset(size.width, 0f))
        }
        y += spacing
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartVerticalAxis() {
    Chart(modifier = Modifier
        .size(100.dp)
        .xRange(0f, 100f)
        .yRange(0f, 100f)
        .plotInset(3.dp)) {
        Plot {
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

        VerticalAxis(Modifier.width(8.dp)) {
            drawTics(10f)
        }
        VerticalAxis(
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
    Chart(
        minX = 0f,
        maxX = 100f,
        minY = 100f,
        maxY = 200f,
        modifier = Modifier
            .size(100.dp)
            .plotInset(3.dp)
    ) {
        Plot {
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

        VerticalAxis(Modifier.width(8.dp)) {
            drawTics(10f)
        }
        VerticalAxis(
            Modifier
                .width(4.dp)
                .background(Color.Cyan), edge = Edge.RIGHT
        ) {
            drawTics(15f)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartVerticalAxisFlip() {
    Chart(modifier = Modifier
        .size(100.dp)
        .xRange(0f, 100f)
        .yRange(100f, 0f)
        .plotInset(3.dp)
    ) {
        Plot {
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

        VerticalAxis(Modifier.width(8.dp)) {
            drawTics(10f)
        }
        VerticalAxis(
            Modifier
                .width(4.dp)
                .background(Color.Cyan), edge = Edge.RIGHT) {
            drawTics(15f)
        }
    }
}