package com.sanbeg.composable_chart.charts

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
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
import com.sanbeg.composable_chart.ComposableChartScaleScope
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.dataSetOf
import com.sanbeg.composable_chart_data.forEach
import com.sanbeg.composable_chart_data.forEachIndexed


/**
 * Because circle's default position ignores any translation and draws at the
 * canvas center, we want to shift the center to match the origin to handle that
 * case.
 */
@JvmInline
private value class OriginCenteredDrawScope(
    private val delegate: DrawScope
) : DrawScope by delegate {
    override val center: Offset
        get() = Offset.Zero
}

private fun ComposableChartScaleScope.drawAt(
    offsets: DataSet,
    content: DrawScope.() -> Unit
) {
    offsets.forEach { offset ->
        if (offset.isSpecified && offset.isFinite) {
            val scaled = scale(offset)
            drawScope.translate(scaled.x, scaled.y, content)
        }
    }
}

/**
 * Draw a scatter plot with the supplied content lambda.
 * The content is called for each data point, and is bound to a [DrawScope] which
 * has both its origin and center at the location of the point.
 */
fun ComposableChartScaleScope.scatter(
    offsets: DataSet,
    content: DrawScope.() -> Unit
) {
    val scope = OriginCenteredDrawScope(drawScope)
    offsets.forEach { offset ->
        if (offset.isSpecified && offset.isFinite) {
            val scaled = scale(offset)
            scope.translate(scaled.x, scaled.y, content)
        }
    }
}

fun ComposableChartScaleScope.scatterWithIndexedValues(
    offsets: DataSet,
    content: DrawScope.(index: Int, offset: Offset) -> Unit
) {
    val scope = OriginCenteredDrawScope(drawScope)
    offsets.forEachIndexed { index, offset ->
        if (offset.isSpecified && offset.isFinite) {
            val scaled = scale(offset)
            scope.translate(scaled.x, scaled.y) {
                content(index, offset)
            }
        }
    }
}

/**
 * Convenience function to draw a scatter diagram as circles.
 *
 * @param data The data which will be plotted, one circle per [Offset]
 * @param radius The radius of each circle
 * @param brush The color or fill to be applied to each circle
 * @param alpha Opacity to be applied to each circle from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param style Whether or not the circles are stroked or filled in
 * @param colorFilter ColorFilter to apply to the [brush] when drawn into the destination
 * @param blendMode Blending algorithm to be applied to the brush
 */
fun ComposableChartScaleScope.scatter(
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
    drawAt(data) {
        drawCircle(brush, r, Offset.Zero, alpha, style, colorFilter, blendMode)
    }
}

/**
 * Convenience function to draw a scatter diagram as circles.
 *
 *  @param data The data which will be plotted, one circle per [Offset]
 *  @param radius The radius of each circle
 *  @param color The color to be applied to each circle
 *  @param alpha Opacity to be applied to each circle from 0.0f to 1.0f representing
 *  fully transparent to fully opaque respectively
 *  @param style Whether or not the circles are stroked or filled in
 *  @param colorFilter ColorFilter to apply to the [color] when drawn into the destination
 *  @param blendMode Blending algorithm to be applied to the brush
 */
fun ComposableChartScaleScope.scatter(
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
                Offset(0f, 0f),
                Offset(25f, 15f),
                Offset(45f, 25f),
                Offset(100f, 100f),
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
                Offset(0f, 0f),
                Offset(25f, 15f),
                Offset(45f, 25f),
                Offset(100f, 100f),
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
                Offset(0f, 0f),
                Offset(25f, 15f),
                Offset(45f, 25f),
                Offset(100f, 100f),
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
        val dataSet = dataSetOf(
            listOf(
                Offset(25f, 25f),
                Offset(0f, 0f),
                Offset(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            drawAt(dataSet) {
                drawCircle(Color.Blue, 4.dp.value, Offset.Zero)
            }
        }
    }
}