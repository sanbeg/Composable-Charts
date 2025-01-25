package com.sanbeg.composable_chart.plots

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.PlotScope
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart.core.drawEachFinite
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.dataSetOf
import com.sanbeg.composable_chart_data.forEach
import com.sanbeg.composable_chart_data.forEachIndexed
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.geometry.isFinite
import com.sanbeg.composable_chart_data.geometry.isSpecified


/**
 * Because circle's default position ignores any translation and draws at the
 * canvas center, we want to shift the center to match the origin to handle that
 * case.
 */
@PublishedApi
@JvmInline
internal value class OriginCenteredDrawScope(
    private val delegate: DrawScope
) : DrawScope by delegate {
    override val center: Offset
        get() = Offset.Zero
}

/**
 * Draw a scatter plot with the supplied content lambda.
 * The content is called for each data point, and is bound to a [DrawScope] which
 * has both its origin and center at the location of the point.
 */
inline fun PlotScope.scatter(
    data: DataSet,
    crossinline content: DrawScope.() -> Unit
) {
    val scope = OriginCenteredDrawScope(drawScope)
    data.forEach { point ->
        if (point.isSpecified && point.isFinite) {
            val scaled = scale(point)
            scope.translate(scaled.x, scaled.y, content)
        }
    }
}

/**
 * Draw a scatter plot with the supplied content lambda.
 * The content is called for each data point, and is bound to a [DrawScope] which
 * has both its origin and center at the location of the point.  Additionally, it
 * will receive both the index and the unscaled [Point], which could be used to
 * render labels.
 */
inline fun PlotScope.scatterWithIndexedValues(
    data: DataSet,
    crossinline content: DrawScope.(index: Int, point: Point) -> Unit
) {
    val scope = OriginCenteredDrawScope(drawScope)
    data.forEachIndexed { index, point ->
        if (point.isSpecified && point.isFinite) {
            val scaled = scale(point)
            scope.translate(scaled.x, scaled.y) {
                content(index, point)
            }
        }
    }
}

/**
 * Convenience function to draw a scatter diagram as circles.
 *
 * @param data The data which will be plotted, one circle per [Point]
 * @param radius The radius of each circle
 * @param brush The color or fill to be applied to each circle
 * @param alpha Opacity to be applied to each circle from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param style Whether or not the circles are stroked or filled in
 * @param colorFilter ColorFilter to apply to the [brush] when drawn into the destination
 * @param blendMode Blending algorithm to be applied to the brush
 */
fun PlotScope.scatter(
    data: DataSet,
    radius: Dp,
    brush: Brush,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode
) {
    val r = with(drawScope) {
        radius.toPx()
    }
    //drawAt(data) {
    //    drawCircle(brush, r, Offset.Zero, alpha, style, colorFilter, blendMode)
    //}
    drawEachFinite(data) { offset ->
        drawCircle(brush, r, offset, alpha, style, colorFilter, blendMode)
    }
}

/**
 * Convenience function to draw a scatter diagram as solid colored circles.
 *
 *  @param data The data which will be plotted, one circle per [Point]
 *  @param radius The radius of each circle
 *  @param color The color to be applied to each circle
 *  @param alpha Opacity to be applied to each circle from 0.0f to 1.0f representing
 *  fully transparent to fully opaque respectively
 *  @param style Whether or not the circles are stroked or filled in
 *  @param colorFilter ColorFilter to apply to the [color] when drawn into the destination
 *  @param blendMode Blending algorithm to be applied to the brush
 */
fun PlotScope.scatter(
    data: DataSet,
    radius: Dp,
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode) =
    scatter(data, radius, SolidColor(color), alpha, style, colorFilter, blendMode)

@Preview(showBackground = true)
@Composable
private fun PreviewScatter() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Point(0f, 0f),
                Point(25f, 15f),
                Point(45f, 25f),
                Point(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            scatter(dataSet, 3.dp, SolidColor(Color.Blue), style=Stroke(2f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScatter2() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Point(0f, 0f),
                Point(25f, 15f),
                Point(45f, 25f),
                Point(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            scatter(dataSet) {
                drawCircle(Color.Blue, 3.dp.toPx())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScatter3() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Point(0f, 0f),
                Point(25f, 15f),
                Point(45f, 25f),
                Point(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            scatter(dataSet) {
                drawRect(Color.Blue,
                    topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                    size = Size(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewDrawAt() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = listOf(
            Point(25f, 25f),
            Point(0f, 0f),
            Point(100f, 100f),
        ).asDataSet()
        Scale(maxY = 100f) {
            scatter(dataSet) {
                drawCircle(Color.Blue, 4.dp.value, Offset.Zero)
            }
        }
    }
}