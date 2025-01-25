package com.sanbeg.composable_chart

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanbeg.composable_chart.core.ModifierLocalDataInset
import com.sanbeg.composable_chart.core.ModifierLocalRangeX
import com.sanbeg.composable_chart.core.plotInset
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.ChartRange
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.geometry.length
import com.sanbeg.composable_chart_data.geometry.max
import com.sanbeg.composable_chart_data.geometry.min

class HorizontalAxisScope internal constructor(
    @PublishedApi
    internal val drawScope: DrawScope,
    val xRange: ChartRange,
    private val dataInset: Float,
) {
    private val scale = (drawScope.size.width - dataInset * 2) / xRange.length()
    fun scale(x: Float): Float = (x - xRange.start) * scale + dataInset
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.HorizontalAxis(
    modifier: Modifier,
    edge: Edge = Edge.BOTTOM,
    content: HorizontalAxisScope.() -> Unit
) {
    var range = ChartRange.Normal
    var inset = 0.dp
    Box(
        modifier
            .modifierLocalConsumer {
                range = ModifierLocalRangeX.current
                inset = ModifierLocalDataInset.current
            }
            .fillMaxWidth()
            .asAxis(edge)
            .drawBehind {
                HorizontalAxisScope(
                    this,
                    range,
                    inset.toPx()
                ).content()
            }
    )
}

inline fun HorizontalAxisScope.drawAt(x: Float, draw: DrawScope.(Float) -> Unit) {
    drawScope.translate(scale(x)) {
        draw(x)
    }
}

fun HorizontalAxisScope.drawTics(spacing: Float) {
    var x = xRange.min()
    while (x <= xRange.max()) {
        drawAt(x) {
            drawLine(Color.Black, Offset.Zero, Offset(0f, size.height))
        }
        x += spacing
    }
}

@SuppressLint("DefaultLocale")
fun HorizontalAxisScope.drawTics(
    spacing: Float,
    textMeasurer: TextMeasurer,
    format: String = "%.2f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    var x = xRange.min()
    while (x <= xRange.max()) {
        drawAt(x) { raw ->
            val s: String = String.format(format, raw)
            val mr = textMeasurer.measure(s, style)
            val ticLength = size.height - mr.size.height
            drawLine(Color.Black, Offset.Zero, Offset(0f, ticLength))
            drawText(mr, Color.Black, topLeft = Offset(-mr.size.width/2f, ticLength))
        }
        x += spacing
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxis() {
    Chart(modifier = Modifier
        .size(100.dp)
        .xRange(0f, 100f)
        .plotInset(3.dp)) {
        Plot(modifier = Modifier.yRange(0f, 100f)) {
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

        val measurer = rememberTextMeasurer()

        HorizontalAxis(Modifier.height(12.dp)) {
            drawTics(20f, measurer, format = "%.0f")
        }
        HorizontalAxis(
            Modifier
                .height(4.dp)
                .background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxisShift() {
    Chart(modifier = Modifier
        .size(100.dp)
        .xRange(100f, 200f)
        .plotInset(3.dp)
    ) {
        Plot(modifier = Modifier.yRange(0f, 100f)) {
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
        HorizontalAxis(
            Modifier
                .height(4.dp)
                .background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxisFlip() {
    Chart(modifier = Modifier
        .size(100.dp)
        .xRange(100f, 0f)
        .plotInset(3.dp)
    ) {
        Plot(modifier = Modifier.yRange(0f, 100f)) {
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
        HorizontalAxis(
            Modifier
                .height(4.dp)
                .background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}