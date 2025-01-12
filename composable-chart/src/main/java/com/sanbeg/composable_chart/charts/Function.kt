package com.sanbeg.composable_chart.charts

import androidx.compose.foundation.interaction.Interaction
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScope
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
    yRange2: ChartRange,
    resolution: Dp,
    content: FunctionScope.() -> Unit
) {
    //val step = with(LocalDensity.current){resolution.toPx()}
    //var step by remember { mutableFloatStateOf(0f) }
    //var step = with(LocalDensity.current) { 0.5.dp.toPx() }
    val step = LocalFunctionResolutionPx.current

    var xRange by remember { mutableStateOf(ChartRange.Normal) }
    var yRange by remember { mutableStateOf(ChartRange.Normal) }

    Box(
        Modifier
            .modifierLocalConsumer {
                //val cur = ModifierLocalResolution.current
                //if (cur > 0) step = cur
                xRange = ModifierLocalRangeX.current
                yRange = ModifierLocalRangeY.current
            }
            .fillMaxSize()
            .asPlot()
            .drawBehind {
                val matrix = Matrix().apply {
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
                FunctionScope(
                    xRange,
                    this,
                    matrix = matrix,
                    resolution = step,
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
        .resolution(.5.dp)
        .xRange(0f, 100f)
        .yRange(-1f, 1f)

    ) {
        Function(ChartRange(-1f, 1f), resolution = 5.dp) {
            plot { x -> sin(x / PI.toFloat().times(2)) }
            //function2 { x -> x / 100f }
            plot { x -> log(x+1f, 2f)/10f }
            plot { _ -> 0f }
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
            Function(ChartRange(-1f, 1f), resolution = 5.dp) {
                plot { x -> sin(x / PI.toFloat().times(2)) }
                //function2 { x -> x / 100f }
                plot { x -> log(x + 1f, 2f) / 10f }
                plot { _ -> 0f }
                //function2 { x -> (x/10f).mod(1f) }
            }
        }
    }
}