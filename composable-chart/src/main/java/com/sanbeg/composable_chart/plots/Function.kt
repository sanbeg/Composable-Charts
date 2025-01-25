package com.sanbeg.composable_chart.plots

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultBlendMode
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScope
import com.sanbeg.composable_chart.core.ModifierLocalDataInset
import com.sanbeg.composable_chart.core.ModifierLocalRangeX
import com.sanbeg.composable_chart.core.ModifierLocalRangeY
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart.makeScaleMatrix
import com.sanbeg.composable_chart_data.geometry.ChartRange
import kotlin.math.PI
import kotlin.math.log
import kotlin.math.sin


class FunctionScope internal constructor(
    //private val chartScope: ComposableChartScope,
    private val xRange: ChartRange,
    @PublishedApi
    internal val drawScope: DrawScope,
    private val matrix: Matrix,
    val resolution: Float,
) {

    //internal val minX by chartScope::minX
    //internal val maxX by chartScope::maxX
    internal val minX by xRange::start
    internal val maxX by xRange::end

    internal val xscale get() = matrix.values[Matrix.ScaleX]
    internal fun map(offset: Offset) = matrix.map(offset)

}

private val ModifierLocalResolution = modifierLocalOf {
    3f
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.resolution(resolution: Dp): Modifier {
    val px = with(LocalDensity.current) {
        resolution.toPx()
    }
    return modifierLocalProvider(ModifierLocalResolution) {
        px
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.Function(
    resolution: Dp = 0.5.dp,
    content: FunctionScope.() -> Unit
) {
    //val step = with(LocalDensity.current){resolution.toPx()}
    //var step by remember { mutableFloatStateOf(0f) }
    //var step = with(LocalDensity.current) { 0.5.dp.toPx() }
    val step = LocalFunctionResolutionPx.current

    var xRange by remember { mutableStateOf(ChartRange.Normal) }
    var yRange by remember { mutableStateOf(ChartRange.Normal) }
    var dataInset by remember{ mutableStateOf(0.dp) }

    Box(
        Modifier
            .modifierLocalConsumer {
                //val cur = ModifierLocalResolution.current
                //if (cur > 0) step = cur
                xRange = ModifierLocalRangeX.current
                yRange = ModifierLocalRangeY.current
                dataInset = ModifierLocalDataInset.current
            }
            .fillMaxSize()
            .asPlot()
            .drawBehind {
                val matrix = makeScaleMatrix(size, dataInset.toPx(), xRange, yRange)

                FunctionScope(
                    xRange,
                    this,
                    matrix = matrix,
                    //resolution = step,
                    resolution = resolution.toPx()
                ).content()
            }
    )
}

fun FunctionScope.plot(function: (x: Float) -> Float) {
    val step = resolution / xscale
    var x = minX
    var prev = Offset.Unspecified
    while (x < maxX) {
        val y = function(x)
        val cur = map(Offset(x,y))
        if (prev.isSpecified) {
            drawScope.drawLine(Color.Black, prev, cur)
        }
        prev = cur
        x += step
    }
}

internal inline fun FunctionScope.drawEachSegment(
    function: (x: Float) -> Float,
    content: DrawScope.(Offset, Offset) -> Unit
) {
    val step = resolution / xscale
    var x = minX
    var prev = Offset.Unspecified
    while (x < maxX) {
        val y = function(x)
        val cur = map(Offset(x,y))
        if (prev.isSpecified) {
            drawScope.content(prev, cur)
        }
        prev = cur
        x += step
    }
}

/**
 * Draws a series of lines connecting the given points using the given paint. The lines
 * are stroked.
 *
 * @param brush the color or fill to be applied to the line
 * @param width stroke width to apply to the line
 * @param pathEffect optional effect or pattern to apply to the line
 * @param alpha opacity to be applied to the [brush] from 0.0f to 1.0f representing
 * fully transparent to fully opaque respectively
 * @param colorFilter ColorFilter to apply to the [brush] when drawn into the destination
 * @param blendMode the blending algorithm to apply to the [brush]
 * @param function the discrete function to draw
 */
fun FunctionScope.line(
    width: Dp = Dp.Hairline,
    brush: Brush = SolidColor(Color.Black),
    pathEffect: PathEffect? = null,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DefaultBlendMode,
    function: (x: Float) -> Float,
    ) {
    drawEachSegment(function) { a, b ->
        drawLine(brush, a, b, width.toPx(), StrokeCap.Round, pathEffect, alpha, colorFilter, blendMode)
    }
}

/*
fun ComposableChartScaleScope.plot(resolution: Float, function: (x: Float) -> Float) {
    val step = resolution / matrix.values[Matrix.ScaleX]
    var x = chartScope.minX
    var prev = Offset.Unspecified
    while (x < chartScope.maxX) {
        val y = function(x)
        val cur = scale(Point(x,y))
        if (prev.isSpecified) {
            drawScope.drawLine(Color.Black, prev, cur)
        }
        prev = cur
        x += step
    }
}
*/

@Preview(showBackground = true)
@Composable
private fun PreviewFunction() {
    Chart(minX = 0f, maxX = 100f, modifier = Modifier
        .size(150.dp)
        //.resolution(.5.dp)
        .xRange(0f, 100f)
        .yRange(-1f, 1f)

    ) {
        Function {
            line { x -> sin(x / PI.toFloat().times(2)) }
            //function2 { x -> x / 100f }
            line { x -> log(x+1f, 2f)/10f }
            line { _ -> 0f }
            //function2 { x -> (x/10f).mod(1f) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFunctionCL() {
    Chart(minX = 0f, maxX = 100f, modifier = Modifier
        .size(150.dp)
        .yRange(-1f, 1f)
        .xRange(0f, 100f)
    ) {
        CompositionLocalProvider(LocalFunctionResolution provides .5.dp) {
            Function(resolution = 5.dp) {
                line { x -> sin(x / PI.toFloat().times(2)) }
                //function2 { x -> x / 100f }
                line { x -> log(x + 1f, 2f) / 10f }
                line { _ -> 0f }
                //function2 { x -> (x/10f).mod(1f) }
            }
        }
    }
}