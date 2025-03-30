package com.sanbeg.composable_chart.axis

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
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
import kotlin.math.min

sealed class VerticalAxisScope(
    @PublishedApi
    internal val drawScope: DrawScope,
    internal val yRange: ChartRange,
    internal val dataInset: Float,
    internal val left: Float,
) {
    private val scale = (drawScope.size.height - dataInset * 2) / -(yRange.end - yRange.start)

    fun scale(y: Float): Float = (y - yRange.end) * scale + dataInset
}

class LeftAxisScope internal constructor(drawScope: DrawScope, yRange: ChartRange, dataInset: Float, left: Float) :
    VerticalAxisScope(drawScope, yRange, dataInset, left)

class RightAxisScope internal constructor(drawScope: DrawScope, yRange: ChartRange, dataInset: Float) :
    VerticalAxisScope(drawScope, yRange, dataInset, 0f)

/**
 * Composable for a vertical axis
 * @param[content] the content which will be drawn.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.LeftAxis(
    modifier: Modifier = Modifier,
    content: LeftAxisScope.() -> Unit
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
            .asAxis(Edge.LEFT)
            .drawBehind {
                LeftAxisScope(
                    this,
                    yrange,
                    inset.toPx(),
                    size.width
                ).content()
            }
    )
}

/**
 * Composable for a vertical axis
 * @param[content] the content which will be drawn.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.RightAxis(
    modifier: Modifier = Modifier,
    content: RightAxisScope.() -> Unit
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
            .asAxis(Edge.RIGHT)
            .drawBehind {
                RightAxisScope(
                    this,
                    yrange,
                    inset.toPx()
                ).content()
            }
    )
}

fun VerticalAxisScope.atPlotLine(draw: DrawScope.() -> Unit) {
    drawScope.translate(left = left) {
        draw()
    }
}

inline fun VerticalAxisScope.drawAt(y: Float, draw: DrawScope.(y: Float) -> Unit) {
    drawScope.translate(top=scale(y)) {
        draw(y)
    }
}

fun VerticalAxisScope.drawTics(spacing: Float) {
    var y = yRange.min()
    while (y <= yRange.max()) {
        drawAt(y) {
            drawLine(Color.Black, Offset.Zero, Offset(size.width, 0f))
        }
        y += spacing
    }
}

/**
 * Draws a vertical line along the edge of the plot using the given paint. The
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
fun VerticalAxisScope.drawPlotLine(
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
        Offset(0f, size.height),
        strokeWidth,
        cap,
        pathEffect,
        alpha,
        colorFilter,
        blendMode
    )
}

/**
 * Draw a set of left tics with text labels
 */
@SuppressLint("DefaultLocale")
fun LeftAxisScope.drawLabelledTics(
    spacing: Float,
    textMeasurer: TextMeasurer,
    color: Color = Color.Black,
    format: String = "%.0f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    drawPlotLine(color)
    var y = yRange.min()
    while (y <= yRange.max()) {
        val s: String = String.format(format, y)
        val mr = textMeasurer.measure(s, style)
        val textHeight = mr.size.height

        drawAt(y) {
            // val ticLength = size.height - mr.size.height
            val ticLength = 3.dp.toPx()
            drawLine(color, Offset(size.width, 0f), Offset(size.width - ticLength, 0f))
            val top = when (y) {
                // if we have vertical axis dataInset may not be needed.
                yRange.min() -> -min(dataInset, textHeight / 2f)
                yRange.max() -> -textHeight.toFloat() + min(dataInset, textHeight / 2f)
                else -> -textHeight / 2f
            }
            drawText(mr, color, topLeft = Offset(size.width - mr.size.width - ticLength, top))
        }
        y += spacing
    }
}

/**
 * Draw a set of right tics with text labels
 */
@SuppressLint("DefaultLocale")
fun RightAxisScope.drawLabelledTics(
    spacing: Float,
    textMeasurer: TextMeasurer,
    color: Color = Color.Black,
    format: String = "%.0f",
    style: TextStyle = TextStyle(fontSize = 6.sp)
) {
    drawPlotLine(color)
    var y = yRange.min()
    while (y <= yRange.max()) {
        val s: String = String.format(format, y)
        val mr = textMeasurer.measure(s, style)
        drawAt(y) {
            // val ticLength = size.height - mr.size.height
            val ticLength = 3.dp.toPx()
            drawLine(color, Offset.Zero, Offset(ticLength, 0f))
            val top = when (y) {
                // if we have vertical axis dataInset may not be needed.
                yRange.min() -> -min(dataInset, mr.size.height / 2f)
                yRange.max() -> -mr.size.height.toFloat() + min(dataInset, mr.size.height / 2f)
                else -> -mr.size.height / 2f
            }
            drawText(mr, color, topLeft = Offset(ticLength, top))
        }
        y += spacing
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLeftTics() {
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
        val measurer = rememberTextMeasurer()
        LeftAxis(Modifier.width(12.dp)) {
            drawLabelledTics(
                spacing = 30f,
                textMeasurer = measurer,
            )
        }
        RightAxis(Modifier.width(12.dp)) {
            drawLabelledTics(
                spacing = 30f,
                textMeasurer = measurer,
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLeftRightTics() {
    Chart(
        modifier = Modifier
            .size(100.dp)
            .xRange(0f, 100f)
            .yRange(0f, 100f)
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
        val measurer = rememberTextMeasurer()
        LeftAxis(Modifier.width(12.dp)) {
            drawLabelledTics(
                spacing = 30f,
                textMeasurer = measurer,
            )
        }
        RightAxis(Modifier.width(12.dp)) {
            drawLabelledTics(
                spacing = 30f,
                textMeasurer = measurer,
            )
        }

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

        LeftAxis(Modifier.width(8.dp)) {
            atPlotLine {
                drawLine(Color.Black, Offset.Zero, Offset(0f, size.height))
            }
            drawTics(10f)
        }
        RightAxis(
            Modifier
                .width(4.dp)
                .background(Color.Cyan)
        ) {
            atPlotLine {
                drawLine(Color.Black, Offset.Zero, Offset(0f, size.height))
            }
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

        LeftAxis(Modifier.width(8.dp)) {
            drawTics(10f)
        }
        val modifier = Modifier
            .width(4.dp)
            .background(Color.Cyan)
        val content = fun VerticalAxisScope.() {
            drawTics(15f)
        }
        RightAxis(modifier, content)
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

        LeftAxis(Modifier.width(8.dp)) {
            drawTics(10f)
        }
        RightAxis(
            Modifier
                .width(4.dp)
                .background(Color.Cyan)
        ) {
            drawTics(15f)
        }
    }
}