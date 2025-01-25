package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import com.sanbeg.composable_chart.core.plotInset
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.Point

/**
 * Specify which edge to draw an axis on
 */
enum class Edge {
    LEFT, RIGHT, TOP, BOTTOM
}

internal sealed interface Role {
    data object Plot : Role
    data class Axis(val edge: Edge, val reserved: Int): Role
    data class Overlay(val alignment: Alignment) : Role
}

private class ChartChildDataElement(
    val role: Role,
    val inspectorInfo: InspectorInfo.() -> Unit,
) : ModifierNodeElement<ChartChildDataNode>() {
    override fun create() = ChartChildDataNode(role)

    override fun update(node: ChartChildDataNode) {
        node.role = role
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun hashCode() = role.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? ChartChildDataElement ?: return false
        return role == otherModifier.role
    }
}

open class ComposableChartScope internal constructor() {

    /**
     * Renders the composable as an axis, on the specified edge.
     * The axis should have a size constraint perpendicular to its edge
     * but be unconstrained in the other direction to match the plot size.
     *
     * This will cause the chart to adapt by either expanding the chart or
     * shrinking the plot so that they can fit.  Note that the axis is allowed
     * to draw outside of its reserved space to overlap with the chart; this
     * can be done be specifying the reserved space, which would override the
     * size.
     *
     * @param[edge] which edge to put this axis on.
     * @param[reserve] the amount of space to reserve for this edge; defaults to unspecified, to use its size.
     */
    @Composable
    @Stable
    fun Modifier.asAxis(
        edge: Edge,
        reserve: Dp = Dp.Unspecified,
    ) : Modifier = this.then(ChartChildDataElement(
        role = Role.Axis(
            edge = edge,
            reserved = if (reserve.isSpecified) {
                with(LocalDensity.current) { reserve.roundToPx() }
            } else {
                -1
            },
        ),
        inspectorInfo = debugInspectorInfo {
            name = "asAxis"
            value = edge
        }
    ))

    /**
     * Render the composable as a plot.  This will place it in the
     * central slot.  If the plot has a constrained size and the chart doesn't,
     * the chart will size itself based on the plot's size.
     */
    @Stable
    fun Modifier.asPlot(): Modifier = this.then(ChartChildDataElement(
        Role.Plot,
        debugInspectorInfo { name = "asPlot" }
    ))

    /**
     * Renders the composable as an overlay.  This renders in the central slot,
     * like the plot.  However, this does not drive the chart's size, so an overlay
     * which fills available size would grow to match the plot.
     */
    @Stable
    fun Modifier.asOverlay(alignment: Alignment) = this.then(ChartChildDataElement(
        Role.Overlay(alignment),
        debugInspectorInfo { name = "asOverlay" }
    ))
}

class LegacyChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
): ComposableChartScope() {
    internal var dataInset: Float = 0f
}

@Composable
fun Chart(
    minX: Float = 0f,
    maxX: Float = 1f,
    dataInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable LegacyChartScope.() -> Unit
) {
    val layoutContent: @Composable () -> Unit = {
        LegacyChartScope(minX, maxX).also {
            it.dataInset = with(LocalDensity.current) { dataInset.toPx() }
        }.content()
    }
    Layout(measurePolicy = ChartMeasurePolicy, content = layoutContent, modifier = modifier)
}

@Composable
fun Chart(
    minX: Float,
    maxX: Float,
    minY: Float,
    maxY: Float,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) = Chart(
    modifier.xRange(minX, maxX).yRange(minY, maxY),
    content
)

/**
 * Composable which lays out the chart and axis.
 *
 * A range can specified in the chart's [modifier] so that all children will use
 * the same range.
 *
 * @see [Modifier.xRange]
 * @see [Modifier.yRange]
 * @see [Modifier.plotInset]
 */
@Composable
fun Chart(
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
){
    val layoutContent: @Composable () -> Unit = {
        ComposableChartScope().content()
    }
    Layout(
        measurePolicy = ChartMeasurePolicy,
        content = layoutContent,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewChart2() {
    Chart(
        modifier = Modifier.size(100.dp).xRange(0f, 100f).yRange(0f,100f).plotInset(6.dp)
    ) {
        Plot(modifier = Modifier) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

            drawEach(
                listOf(
                    Point(25f, 25f),
                    Point(0f, 0f),
                    Point(100f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 6.dp.value, it)
            }
        }
        Spacer(
            Modifier
                .asAxis(Edge.BOTTOM)
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.Red))
    }
}
