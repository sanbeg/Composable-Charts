package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.ModifierLocalDataInset
import com.sanbeg.composable_chart.core.ModifierLocalRangeX
import com.sanbeg.composable_chart.core.ModifierLocalRangeY
import com.sanbeg.composable_chart_data.geometry.ChartRange

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

/**
 * A composable which provides a drawing surface with scaling for its content.  The content is
 * invoked in a scope with functionality to evaluate discrete functions.
 *
 * @param[modifier] The modifier to apply to the scale.
 * @param[resolution] The resolution to evaluate the function; smaller values will give a smoother rendering.
 * @param[content] The content of the Scale.
 *
 * @see [xRange]
 * @see [yRange]
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComposableChartScope.Function(
    modifier: Modifier = Modifier,
    resolution: Dp = 0.5.dp,
    content: FunctionScope.() -> Unit
) {
    //val step = with(LocalDensity.current){resolution.toPx()}
    //var step by remember { mutableFloatStateOf(0f) }
    //var step = with(LocalDensity.current) { 0.5.dp.toPx() }
    //val step = LocalFunctionResolutionPx.current

    var xRange by remember { mutableStateOf(ChartRange.Normal) }
    var yRange by remember { mutableStateOf(ChartRange.Normal) }
    var dataInset by remember { mutableStateOf(0.dp) }

    Box(
        modifier
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