package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.charts.ModifierLocalDataInset
import com.sanbeg.composable_chart.charts.ModifierLocalLogBase
import com.sanbeg.composable_chart.charts.ModifierLocalRangeX
import com.sanbeg.composable_chart.charts.ModifierLocalRangeY
import com.sanbeg.composable_chart.charts.dataInset
import com.sanbeg.composable_chart.charts.logScale
import com.sanbeg.composable_chart.charts.xRange
import com.sanbeg.composable_chart.charts.yRange
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.ChartRange
import com.sanbeg.composable_chart_data.geometry.FloatPair
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.geometry.isSpecified
import kotlin.math.log

class ComposableChartScaleScope internal constructor(
    private val matrix: Matrix,
    private val logBase: FloatPair,
    @PublishedApi
    internal val drawScope: DrawScope,
) {
    @PublishedApi
    internal fun scale(point: Point) = if (point.isSpecified) {
        if (logBase.isSpecified) {
            val x = if (logBase.first > 0) log(point.x, logBase.first) else point.x
            val y = if (logBase.second > 0) log(point.y, logBase.second) else point.y
            matrix.map(Offset(x,y))
        } else {
            matrix.map(Offset(point.x, point.y))
        }
    } else {
        Offset(point.x, point.y)
    }
}

/**
 * A composable which provides a scaling for its content.  The content is invoked in a scope which
 * provides functionality to scale [Point]s in real-world units to [Offset]s in pixels.
 *
 * @param[minY] The Y value which will scale to the bottom of the chart.
 * @param[maxY] The Y value which will scale to the top of the chart.
 *
 * Note that [minY] and [maxY] should not be the same.  If [minY] < [maxY], then increasing values
 * will be drawn closer to the top.  Swapping [minY] and [maxY] would cause the graph to be inverted.
 *
 * @param[modifier] The modifier to apply to the scale.
 * @param[content] The content of the Scale.
 *
 */
@Composable
fun ComposableChartScope.Scale(
    minY: Float = 0f,
    maxY: Float = 1f,
    modifier: Modifier = Modifier,
    content: ComposableChartScaleScope.() -> Unit
    ) {
    Spacer(
        modifier
            .asPlot()
            .fillMaxSize()
            .drawBehind {
                val matrix = Matrix().apply {
                    translate(x = dataInset, y = dataInset)
                    val di2 = dataInset * 2
                    scale(
                        x = (size.width - di2) / (maxX - minX),
                        y = (size.height - di2) / -(maxY - minY),
                    )
                    translate(
                        x = -minX,
                        y = -maxY,
                    )
                }
                ComposableChartScaleScope(matrix, FloatPair.Unspecified, this).content()
            })
}

internal fun makeScaleMatrix(size: Size, dataInset: Float, xRange: ChartRange, yRange: ChartRange) = Matrix().apply {
    translate(x = dataInset, y = dataInset)
    val di2 = dataInset * 2
    scale(
        x = (size.width - di2) / (xRange.end - xRange.start),
        y = (size.height - di2) / -(yRange.end - yRange.start),
    )
    translate(
        x = -xRange.start,
        y = -yRange.end,
    )
}

internal fun setScaleMatrix(matrix: Matrix, size: Size, dataInset: Float, xRange: ChartRange, yRange: ChartRange) = matrix.apply {
    reset()
    translate(x = dataInset, y = dataInset)
    val di2 = dataInset * 2
    scale(
        x = (size.width - di2) / (xRange.end - xRange.start),
        y = (size.height - di2) / -(yRange.end - yRange.start),
    )
    translate(
        x = -xRange.start,
        y = -yRange.end,
    )
}

/**
 * A composable which provides a scaling for its content.  The content is invoked in a scope which
 * provides functionality to scale [Point]s in real-world units to [Offset]s in pixels.
 *
 * @param[modifier] The modifier to apply to the scale.
 * @param[content] The content of the Scale.
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.Plot(
    modifier: Modifier = Modifier,
    content: ComposableChartScaleScope.() -> Unit
) {
    var xRange by remember { mutableStateOf(ChartRange.Normal) }
    var yRange by remember { mutableStateOf(ChartRange.Normal) }
    var dataInset by remember { mutableFloatStateOf(0f) }
    var logScale by remember { mutableStateOf(FloatPair.Unspecified) }
    val matrix = remember { Matrix() }

    Box(modifier
        .asPlot()
        .fillMaxSize()
        .modifierLocalConsumer {
            xRange = ModifierLocalRangeX.current
            yRange = ModifierLocalRangeY.current
            dataInset = ModifierLocalDataInset.current
            logScale = ModifierLocalLogBase.current
        }
        .drawBehind {
            setScaleMatrix(matrix, size, dataInset, xRange, yRange)
            ComposableChartScaleScope(matrix, logScale, this).content()
        })
}

@Preview(showBackground = true)
@Composable
private fun PreviewChart() {
    Chart(maxX = 100f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

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
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartFlip() {
    Chart(maxX = 100f, dataInset = 4.dp,
        modifier = Modifier
            .size(100.dp)
            .xRange(0f, 100f)
            .yRange(100f, 0f)
            .dataInset(4.dp)
    ) {
        Plot {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(
                listOf(
                    Point(25f, 25f),
                    Point(0f, 0f),
                    Point(100f, 100f),
                    ).asDataSet()
            ) {
                drawCircle(Color.Blue, 4.dp.toPx(), it)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewPlot() {
    Chart(maxX = 100f, dataInset = 3.dp,
        modifier = Modifier
            .size(100.dp)
            .xRange(0f, 100f)
            .yRange(0f, 100f)
            .dataInset(3.dp)
            //.logScale(y=2f)
    ) {
        Plot {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

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
    }
}