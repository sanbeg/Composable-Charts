package com.sanbeg.composable_chart.axis

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScope
import com.sanbeg.composable_chart.Edge
import com.sanbeg.composable_chart.Plot
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
import kotlin.math.min

// todo - add log scale?
class HorizontalAxisScope internal constructor(
    @PublishedApi
    internal val drawScope: DrawScope,
    val xRange: ChartRange,
    internal val dataInset: Float,
    @PublishedApi
    internal val top: Float,
) {
    private val scale = (drawScope.size.width - dataInset * 2) / xRange.length()
    fun scale(x: Float): Float = (x - xRange.start) * scale + dataInset
}

/**
 * Composable for a horizontal axis
 * @param[edge] which edge to place the axis, defaults to [Edge.BOTTOM], but can also specify [Edge.TOP]
 * @param[content] the content which will be drawn.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.HorizontalAxis(
    modifier: Modifier,
    edge: Edge = Edge.BOTTOM,
    content: HorizontalAxisScope.() -> Unit
) {
    // todo - support reserve height
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
                    inset.toPx(),
                    if (edge == Edge.TOP) size.height else 0f
                ).content()
            }
    )
}

inline fun HorizontalAxisScope.drawAt(x: Float, draw: DrawScope.() -> Unit) {
    drawScope.translate(scale(x)) {
        draw()
    }
}

// todo - consider direction, reserved space to get to plot line.
inline fun HorizontalAxisScope.atPlotLine(draw: DrawScope.() -> Unit) {
    drawScope.translate(top = top) {
        draw()
    }
}

/**
 * Draws a horizontal line along the edge of the plot using the given paint. The
 * line is stroked.
 *
 * @param color the color to be applied to the line
 * @param strokeWidth The stroke width to apply to the line
 * @param cap treatment applied to the ends of the line segment
 * @param pathEffect optional effect or pattern to apply to the line
 * @param alpha opacity to be applied to the [color] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [color] when drawn into the destination
 * @param blendMode the blending algorithm to apply to the [color]
 */
fun HorizontalAxisScope.drawPlotLine(
    color: Color,
    strokeWidth: Float = Stroke.HairlineWidth,
    cap: StrokeCap = Stroke.DefaultCap,
    pathEffect: PathEffect? = null,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) = atPlotLine {
    drawLine(
        color,
        Offset.Zero,
        Offset(size.width, 0f),
        strokeWidth,
        cap,
        pathEffect,
        alpha,
        colorFilter,
        blendMode
    )
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

/**
 * Draw a set of bottom tics with text labels
 */
@SuppressLint("DefaultLocale")
fun HorizontalAxisScope.drawBottomTics(
    spacing: Float,
    textMeasurer: TextMeasurer,
    color: Color = Color.Black,
    format: String = "%.2f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    drawPlotLine(color)
    var x = xRange.min()
    while (x <= xRange.max()) {
        val s: String = String.format(format, x)
        val mr = textMeasurer.measure(s, style)
        drawAt(x) {
            // val ticLength = size.height - mr.size.height
            val ticLength = 3.dp.toPx()
            drawLine(color, Offset.Zero, Offset(0f, ticLength))
            val left = when (x) {
                // if we have vertical axis dataInset may not be needed.
                xRange.min() -> -min(dataInset, mr.size.width / 2f)
                xRange.max() -> -mr.size.width.toFloat() + min(dataInset, mr.size.width / 2f)
                else -> -mr.size.width / 2f
            }
            drawText(mr, color, topLeft = Offset(left, ticLength))
        }
        x += spacing
    }
}

@SuppressLint("DefaultLocale")
fun HorizontalAxisScope.drawBottomTicsInside(
    spacing: Float,
    textMeasurer: TextMeasurer,
    color: Color = Color.Black,
    format: String = "%.2f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    drawPlotLine(color)
    var x = xRange.min()
    while (x <= xRange.max()) {
        val s: String = String.format(format, x)
        val mr = textMeasurer.measure(s, style)
        drawAt(x) {
            // val ticLength = size.height - mr.size.height
            val ticLength = 3.dp.toPx()
            drawLine(color, Offset.Zero, Offset(0f, -ticLength))
            val left = when (x) {
                // if we have vertical axis dataInset may not be needed.
                xRange.min() -> -min(dataInset, mr.size.width / 2f)
                xRange.max() -> -mr.size.width.toFloat() + min(dataInset, mr.size.width / 2f)
                else -> -mr.size.width / 2f
            }
            drawText(mr, color, topLeft = Offset(left, ticLength))
        }
        x += spacing
    }
}

/**
 * Draw a set of top tics with text labels
 */
@SuppressLint("DefaultLocale")
fun HorizontalAxisScope.drawTopTics(
    spacing: Float,
    textMeasurer: TextMeasurer,
    color: Color = Color.Black,
    format: String = "%.2f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    drawPlotLine(color)
    var x = xRange.min()
    while (x <= xRange.max()) {
        val s: String = String.format(format, x)
        val mr = textMeasurer.measure(s, style)
        drawAt(x) {
            val ticLength = 3.dp.toPx()
            val left = when (x) {
                xRange.min() -> -min(dataInset, mr.size.width / 2f)
                xRange.max() -> -mr.size.width.toFloat() + min(dataInset, mr.size.width / 2f)
                else -> -mr.size.width / 2f
            }
            //drawLine(color, Offset(0f, size.height), Offset(0f, mr.size.height.toFloat()))
            drawLine(color, Offset(0f, size.height), Offset(0f, size.height - ticLength))

            drawText(mr, color, topLeft = Offset(left, size.height - mr.size.height - ticLength))
        }
        x += spacing
    }
}

/**
 * Draw a set of top tics with text labels
 */
@SuppressLint("DefaultLocale")
fun HorizontalAxisScope.drawTopTicsInside(
    spacing: Float,
    textMeasurer: TextMeasurer,
    color: Color = Color.Black,
    format: String = "%.2f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    drawPlotLine(color)
    var x = xRange.min()
    while (x <= xRange.max()) {
        val s: String = String.format(format, x)
        val mr = textMeasurer.measure(s, style)
        drawAt(x) {
            val ticLength = 3.dp.toPx()

            val left = when (x) {
                xRange.min() -> -min(dataInset, mr.size.width / 2f)
                xRange.max() -> -mr.size.width.toFloat() + min(dataInset, mr.size.width / 2f)
                else -> -mr.size.width / 2f
            }
            //drawLine(color, Offset(0f, size.height), Offset(0f, mr.size.height.toFloat()))
            drawLine(color, Offset(0f, size.height), Offset(0f, size.height + ticLength))

            drawText(mr, color, topLeft = Offset(left, size.height - mr.size.height - ticLength))
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

            drawBottomTics(20f, measurer, format = "%.0f")
        }
        HorizontalAxis(
            Modifier
                .height(12.dp)
                .background(Color.Cyan),
            edge = Edge.TOP
        ) {
            drawTopTics(20f, measurer, format = "%.0f")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHorizontalAxisInside() {
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

            drawBottomTicsInside(20f, measurer, format = "%.0f")
        }
        HorizontalAxis(
            Modifier
                .height(12.dp)
                .background(Color.Cyan),
            edge = Edge.TOP
        ) {
            drawTopTicsInside(20f, measurer, format = "%.0f")
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
                .background(Color.Cyan), edge = Edge.TOP
        ) {
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
                .background(Color.Cyan), edge = Edge.TOP
        ) {
            drawTics(15f)
        }
    }
}