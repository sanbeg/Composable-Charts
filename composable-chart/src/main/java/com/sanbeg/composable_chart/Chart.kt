package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.util.fastForEachIndexed
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.Point
import kotlin.math.max

/**
 * Specify which edge to draw an axis on
 */
enum class Edge {
    LEFT, RIGHT, TOP, BOTTOM
}

private sealed interface Role {
    data object Plot : Role
    data class Axis(val edge: Edge, val reserved: Int): Role
    data class Overlay(val alignment: Alignment) : Role
}

class ComposableChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
    ) {
    internal var dataInset: Float = 0f

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

/*************************************
 * boilerplate for chart layout, based on Box
 *************************************/

private class ChartMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        if (measurables.isEmpty()) {
            return layout(constraints.minWidth, constraints.minHeight) {}
        }
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        if (measurables.size == 1) {
            val measurable = measurables[0]
            val placeable = measurable.measure(contentConstraints)
            return layout(placeable.width, placeable.height) {
                placeable.place(0, 0)
            }
        }

        var topReservation = 0
        var bottomReservation = 0
        var leftReservation = 0
        var rightReservation = 0

        /*
         * pass 1 - determine reserved space based on intrinsic measurements
         */
        measurables.fastForEachIndexed{ index, measurable ->
            if (measurable.isAxis) {
                var reserve = measurable.axisRole?.reserved ?: 0

                measurable.axisRole?.edge?.let {
                    if (reserve < 0) {
                        if ((it == Edge.TOP || it == Edge.BOTTOM)) {
                            reserve = measurable.minIntrinsicHeight(constraints.maxWidth)
                        }
                        if ((it == Edge.LEFT || it == Edge.RIGHT)) {
                            reserve = measurable.minIntrinsicWidth(constraints.maxHeight)
                        }
                    }
                    when(it) {
                        Edge.TOP -> topReservation = max(topReservation, reserve)
                        Edge.BOTTOM -> bottomReservation = max(bottomReservation, reserve)
                        Edge.LEFT -> leftReservation = max(leftReservation, reserve)
                        Edge.RIGHT -> rightReservation = max(rightReservation, reserve)
                        else -> {}
                    }
                }
            }
        }

        val verticalReservation = topReservation + bottomReservation
        val horizontalReservation = leftReservation + rightReservation

        val horizAxisConstraint = if (horizontalReservation > 0) {
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = constraints.maxWidth - horizontalReservation,
            )
        } else {
            contentConstraints
        }

        val vertAxisConstraint = if (verticalReservation > 0) {
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxHeight = constraints.maxHeight - verticalReservation,
            )
        } else {
            contentConstraints
        }

        val plotAreaConstraints = constraints.copy(
            minWidth = constraints.minWidth.minus(horizontalReservation).coerceAtLeast(0),
            minHeight = constraints.minHeight.minus(verticalReservation).coerceAtLeast(0),
            maxHeight = if (verticalReservation < constraints.maxHeight) constraints.maxHeight - verticalReservation else constraints.maxHeight,
            maxWidth = if (horizontalReservation < constraints.maxWidth) constraints.maxWidth - horizontalReservation else constraints.maxWidth,
            )

        var plotWidth = 0
        var plotHeight = 0

        // pass 2 - measure axis & plots
        val placeables = Array(measurables.size) {index ->
            val measurable = measurables[index]
            if (measurable.isAxis) {
                val axisConstraint = when(measurable.axisRole?.edge) {
                    Edge.BOTTOM, Edge.TOP -> horizAxisConstraint
                    else -> vertAxisConstraint
                }
                measurable.measure(axisConstraint)
            } else if (measurable.chartRole == Role.Plot) { // TODO - separate plot from random non-slot children?
                val placeable = measurable.measure(plotAreaConstraints)
                plotWidth = max(plotWidth, placeable.width)
                plotHeight = max(plotHeight, placeable.height)
                placeable
            } else {
                measurable.measure(plotAreaConstraints)
            }
        }

        val chartWidth = plotWidth + horizontalReservation
        val chartHeight = plotHeight + verticalReservation

        // pass 3 - determine position for each child
        val positions = Array(measurables.size) { index ->
            val measurable = measurables[index]
            if (measurable.isAxis) {
                /*
                 * since axis are allowed to draw outside of their reserved space,
                 * we draw them in most of the chart space with an appropriate alignment.
                 */
                val alignment = measurable.axisAlignment ?: Alignment.Center
                val placeable = placeables[index]
                val offset: IntOffset
                val space: IntSize
                val size: IntSize
                measurable.axisRole?.edge.let {
                    when(it) {
                        Edge.TOP, Edge.BOTTOM -> {
                            offset = IntOffset(leftReservation, 0)
                            space = IntSize(plotWidth, chartHeight)
                            size = IntSize(plotWidth, placeable.height)
                        }
                        Edge.LEFT, Edge.RIGHT-> {
                            offset = IntOffset(0, topReservation)
                            space = IntSize(chartWidth, plotHeight)
                            size = IntSize(placeable.width, plotHeight)
                        }
                        null -> {
                            offset = IntOffset.Zero
                            space = IntSize.Zero
                            size = IntSize.Zero
                        }
                    }
                }
                alignment.align(size, space, LayoutDirection.Ltr).plus(offset)
            } else if (measurable.chartRole is Role.Overlay) {
                val placeable = placeables[index]
                val alignment = (measurable.chartRole as? Role.Overlay)?.alignment ?: Alignment.TopStart
                val size = IntSize(placeable.width, placeable.height)
                val space = IntSize(plotWidth, plotHeight)
                val offset = IntOffset(leftReservation, topReservation)
                alignment.align(size, space, LayoutDirection.Ltr).plus(offset)
            } else {
                IntOffset(leftReservation, topReservation)
            }
        }

        return layout(chartWidth, chartHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable.place(positions[index])
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartLayout() {
    Chart(modifier = Modifier.size(50.dp)) {
        Spacer(modifier = Modifier
            .size(10.dp)
            .background(Color.Red)
            .asAxis(edge = Edge.BOTTOM, reserve = Dp.Unspecified))
        Spacer(modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(alpha = 0.5f))
            .asPlot()
        )

        Spacer(modifier = Modifier
            .size(5.dp)
            .asAxis(edge = Edge.TOP, reserve = Dp.Unspecified)
            .background(Color.Red))
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartWrap1() {
    Chart(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier
            .size(50.dp)
            .background(Color.Blue))
    }
}


@Preview(showBackground = true)
@Composable
private fun TestChartWrap2() {
    Chart(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier
            .asPlot()
            .size(50.dp)
            .background(Color.Blue))
        Spacer(modifier = Modifier
            .size(10.dp)
            .background(Color.Red)
            .asAxis(Edge.BOTTOM))
    }
}


@Preview(showBackground = true)
@Composable
private fun TestChartWrap4() {
    Chart {
        Spacer(modifier = Modifier
            .asPlot()
            .size(30.dp)
            .background(Color.Blue))
        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.Red)
            .asAxis(Edge.BOTTOM))
        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.Red)
            .asAxis(Edge.TOP))
        Spacer(modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(Color.Red)
            .asAxis(Edge.LEFT))
        Spacer(modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(Color.Red)
            .asAxis(Edge.RIGHT))

        Spacer(
            modifier = Modifier
                .padding(2.dp)
                .size(3.dp)
                .background(Color.Green, CircleShape)
                .asOverlay(Alignment.TopEnd)
        )
/*
        Box(Modifier.fillMaxSize().padding(2.dp)) {
            Spacer(
                modifier = Modifier
                    .size(3.dp)
                    .background(Color.Green, CircleShape)
                    .align(Alignment.TopEnd)
            )
        }
*/
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartWrap4_2() {
    Chart(modifier= Modifier.size(40.dp)) {
        Spacer(modifier = Modifier
            .asPlot()
            .background(Color.Blue))
        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.Red)
            .asAxis(Edge.BOTTOM))
        Spacer(modifier = Modifier
            .height(8.dp)
            .fillMaxWidth()
            .background(Color.Red)
            .asAxis(Edge.TOP))
        Spacer(modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(Color.Red)
            .asAxis(Edge.LEFT))
        Spacer(modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .background(Color.Red)
            .asAxis(Edge.RIGHT))

        // this works if chart has specified size
        Box(Modifier.padding(2.dp)) {
            Spacer(
                modifier = Modifier
                    .size(3.dp)
                    .background(Color.Green, CircleShape)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartInBox() {
    Box(Modifier.size(75.dp)) {
        Chart(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier
                .asPlot()
                .fillMaxSize()
                .background(Color.Blue))
            Spacer(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red)
                    .asAxis(Edge.BOTTOM)
            )
        }
    }
}


private val Measurable.chartChildDataNode: ChartChildDataNode? get() = parentData as? ChartChildDataNode
private val Measurable.chartRole: Role? get() = (parentData as? ChartChildDataNode)?.role
private val Measurable.isAxis: Boolean get() = chartRole is Role.Axis
private val Measurable.axisRole: Role.Axis? get() = chartRole as? Role.Axis
private val Measurable.axisAlignment: Alignment? get() = (chartRole as? Role.Axis)?.edge?.let {
    when(it) {
        Edge.LEFT -> Alignment.TopStart
        Edge.RIGHT -> Alignment.TopEnd
        Edge.TOP -> Alignment.TopStart
        Edge.BOTTOM -> Alignment.BottomStart
    }
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

private class ChartChildDataNode(
    var role: Role
) : ParentDataModifierNode, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@ChartChildDataNode
}

@Composable
fun Chart(
    minX: Float = 0f,
    maxX: Float = 1f,
    dataInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) {
    val layoutContent: @Composable () -> Unit = {
        ComposableChartScope(minX, maxX).also {
            it.dataInset = with(LocalDensity.current) { dataInset.toPx() }
        }.content()
    }
    Layout(measurePolicy = ChartMeasurePolicy(), content = layoutContent, modifier = modifier)
}

@Preview(showBackground = true)
@Composable
private fun PreviewChart2() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
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
