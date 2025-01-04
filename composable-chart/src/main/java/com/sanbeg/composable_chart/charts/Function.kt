package com.sanbeg.composable_chart.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScope
import kotlin.math.PI
import kotlin.math.log
import kotlin.math.sin


class FunctionScope internal constructor(
    private val chartScope: ComposableChartScope,
    @PublishedApi
    internal val drawScope: DrawScope,
    val matrix: Matrix,
    val resolution: Float,
    val minY: Float,
    val maxY: Float,
) {

    internal val minX by chartScope::minX
    internal val maxX by chartScope::maxX

    internal val xscale = (drawScope.size.width - chartScope.dataInset * 2) / (maxX - minX)
    private val yscale = (drawScope.size.height - chartScope.dataInset * 2) / -(maxY - minY)


    fun scale(x: Float): Float = (x - chartScope.minX) * xscale + chartScope.dataInset
    fun scaleY(y: Float): Float = (y - maxY) * yscale + chartScope.dataInset

    fun invScale(r: Float) = (r - chartScope.dataInset) / xscale + chartScope.minX
}

@Composable
fun ComposableChartScope.Function(
    minY: Float,
    maxY: Float,
    resolution: Dp,
    content: FunctionScope.() -> Unit
) {
    val step = with(LocalDensity.current){resolution.toPx()}

    Box(Modifier
        .fillMaxSize()
        .asPlot()
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
            FunctionScope(
                this@Function,
                this,
                matrix = matrix,
                resolution = step,
                minY = minY,
                maxY = maxY,
                ).content()
        }
    )
}

fun FunctionScope.function(function: (x: Float) -> Float) {
    var xpx = scale(minX)
    val endX = scale(maxX)
    var prev = Offset.Unspecified
    while (xpx < endX) {
        val x = invScale(xpx)
        val y = function(x)
        val ypx = scaleY(y)

        val cur = Offset(xpx, ypx)
        if (prev.isSpecified) {
            drawScope.drawLine(Color.Black, prev, cur)
        }
        prev = cur
        xpx += resolution
    }
}

fun FunctionScope.function2(function: (x: Float) -> Float) {
    val step = resolution / xscale
    var x = minX
    var prev = Offset.Unspecified
    while (x < maxX) {
        val y = function(x)
        val xpx = scale(x)
        val ypx = scaleY(y)

        val cur = Offset(xpx, ypx)
        if (prev.isSpecified) {
            drawScope.drawLine(Color.Black, prev, cur)
        }
        prev = cur
        x += step
    }
}

fun FunctionScope.plot(function: (x: Float) -> Float) {
    val step = resolution / matrix.values[Matrix.ScaleX]
    var x = minX
    var prev = Offset.Unspecified
    while (x < maxX) {
        val y = function(x)
        val cur = matrix.map(Offset(x,y))
        if (prev.isSpecified) {
            drawScope.drawLine(Color.Black, prev, cur)
        }
        prev = cur
        x += step
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFunction() {
    Chart(minX = 0f, maxX = 100f, modifier = Modifier.size(150.dp)) {
        Function(minY = -1f, maxY = 1f, resolution = .5.dp) {
            plot { x -> sin(x / PI.toFloat().times(2)) }
            //function2 { x -> x / 100f }
            plot { x -> log(x+1f, 2f)/10f }
            function2 { _ -> 0f }
            //function2 { x -> (x/10f).mod(1f) }
        }
    }
}