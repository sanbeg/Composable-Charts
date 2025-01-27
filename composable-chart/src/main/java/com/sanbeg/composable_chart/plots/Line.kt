package com.sanbeg.composable_chart.plots

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.PlotScope
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart.core.drawEachSegment
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.dataSetOf
import com.sanbeg.composable_chart_data.geometry.Point

/**
 * Draws a series of lines connecting the given points using the given paint. The lines
 * are stroked.
 *
 * @param data the set of points
 * @param brush the color or fill to be applied to the line
 * @param width stroke width to apply to the line
 * @param pathEffect optional effect or pattern to apply to the line
 * @param alpha opacity to be applied to the [brush] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [brush] when drawn into the destination
 * @param blendMode the blending algorithm to apply to the [brush]
 */
fun PlotScope.line(
    data: DataSet,
    width: Dp = Dp.Hairline,
    brush: Brush,
    pathEffect: PathEffect? = null,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
    ) {
    drawEachSegment(data) { a, b ->
        drawLine(brush, a, b, width.toPx(), StrokeCap.Round, pathEffect, alpha, colorFilter, blendMode)
    }
}

// see https://matplotlib.org/stable/gallery/lines_bars_and_markers/stairs_demo.html
/**
 * Enum specifying whether the step function should be drawn with the vertical line
 * before or after the horizontal
 */
enum class StepVertical {
    Pre,
    Post,
}

inline fun PlotScope.step(
    data: DataSet,
    where: StepVertical = StepVertical.Post,
    crossinline content: DrawScope.(start: Offset, end: Offset) -> Unit
) {
    drawEachSegment(data) { a, c ->
        val b = when (where) {
            StepVertical.Pre -> Offset(a.x, c.y)
            StepVertical.Post -> Offset(c.x, a.y)
        }
        content(a, b)
        content(b, c)
    }
}

/**
 * Draws a series of lines connecting the given points with horizontal and vertical lines using the
 * given paint. The lines are stroked.
 *
 * @param data the set of points
 * @param brush the color or fill to be applied to the line
 * @param where where to draw the vertical line
 * @param width stroke width to apply to the line
 * @param pathEffect optional effect or pattern to apply to the line
 * @param alpha opacity to be applied to the [brush] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [brush] when drawn into the destination
 * @param blendMode the blending algorithm to apply to the [brush]
 */
fun PlotScope.step(
    data: DataSet,
    width: Dp = Dp.Hairline,
    brush: Brush,
    where: StepVertical = StepVertical.Post,
    pathEffect: PathEffect? = null,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    step(data, where) { a, b ->
        drawLine(brush, a, b, width.toPx(), StrokeCap.Round, pathEffect, alpha, colorFilter, blendMode)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLine() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Point(0f, 0f),
                Point(25f, 20f),
                Point(45f, 25f),
                Point(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            line(dataSet, 1.dp, SolidColor(Color.Blue))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStep() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Point(0f, 0f),
                Point(25f, 20f),
                Point(45f, 25f),
                Point(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            //line(dataSet, 1.dp, SolidColor(Color.Cyan))
            drawEachSegment(dataSet) {a, b ->
                drawLine(Color.Cyan, a, b,
                    pathEffect = dashPathEffect(floatArrayOf(6f, 3f)))
            }
            step(dataSet, 1.dp, SolidColor(Color.Blue))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStepPre() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Point(0f, 0f),
                Point(25f, 20f),
                Point(45f, 25f),
                Point(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            line(dataSet, 1.dp, SolidColor(Color.Cyan))
            step(dataSet, 1.dp, SolidColor(Color.Blue), where=StepVertical.Pre)
        }
    }
}