package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
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
import kotlin.math.max

enum class Slot {
    LEFT, RIGHT, TOP, BOTTOM, CENTER
}

class ComposableChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
    ) {
    internal var dataInset: Float = 0f

    @Composable
    @Stable
    fun Modifier.asAxis(
        slot: Slot,
        reserve: Dp = Dp.Unspecified,
    ) : Modifier = this.then(ChartChildDataElement(
        slot = slot,
        isAxis = (slot != Slot.CENTER),
        reserved = if (reserve.isSpecified) {
            with(LocalDensity.current) { reserve.roundToPx() }
        } else {
            -1
        },
        inspectorInfo = debugInspectorInfo {
            name = "asAxis"
            value = slot
        }
    ))
}

@Composable
fun Chart(
    minX: Float = 0f,
    maxX: Float = 1f,
    dataInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) = ChartLayout(minX, maxX, dataInset, modifier, content)

@Composable
fun Chart1(
    minX: Float = 0f,
    maxX: Float = 1f,
    dataInset: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable ComposableChartScope.() -> Unit
) {
    Box(modifier) {
        ComposableChartScope(minX, maxX).also {
            it.dataInset = with(LocalDensity.current) { dataInset.toPx() }
        }.content()
    }
}

/*************************************
 * boilerplate for new chart layout, based on Box
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

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val positions = arrayOfNulls<IntOffset>(measurables.size)

        var topReservation = 0
        var bottomReservation = 0
        var leftReservation = 0
        var rightReservation = 0

        /*
         * pass 1 - determine reserved space based on intrinsic measurements
         */
        measurables.fastForEachIndexed{ index, measurable ->
            if (measurable.isAxis) {
                var reserve = measurable.chartChildDataNode?.reserved ?: 0

                measurable.slot?.let {
                    if (reserve < 0) {
                        if ((it == Slot.TOP || it == Slot.BOTTOM)) {
                            reserve = measurable.minIntrinsicHeight(constraints.maxWidth)
                        }
                        if ((it == Slot.LEFT || it == Slot.RIGHT)) {
                            reserve = measurable.minIntrinsicWidth(constraints.maxHeight)
                        }
                    }
                    when(it) {
                        Slot.TOP -> topReservation = max(topReservation, reserve)
                        Slot.BOTTOM -> bottomReservation = max(bottomReservation, reserve)
                        Slot.LEFT -> leftReservation = max(leftReservation, reserve)
                        Slot.RIGHT -> rightReservation = max(rightReservation, reserve)
                        else -> {}
                    }
                }
            }
        }

        val verticalReservation = topReservation + bottomReservation
        val horizontalReservation = leftReservation + rightReservation

        val horizAxisonstraint = constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxWidth = constraints.maxWidth - horizontalReservation,
        )
        val vertAxisConstraint = constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxHeight = constraints.maxHeight - verticalReservation,
        )

        val plotAreaConstraints = constraints.copy(
            minWidth = constraints.minWidth.minus(horizontalReservation).coerceAtLeast(0),
            minHeight = constraints.minHeight.minus(verticalReservation).coerceAtLeast(0),
            maxHeight = if (verticalReservation < constraints.maxHeight) constraints.maxHeight - verticalReservation else constraints.maxHeight,
            maxWidth = if (horizontalReservation < constraints.maxWidth) constraints.maxWidth - horizontalReservation else constraints.maxWidth,
            )

        var plotWidth = 0
        var plotHeight = 0

        // pass 2 - measure axis & plots
        measurables.fastForEachIndexed{ index, measurable ->
            if (measurable.isAxis) {
                val axisConstraint = when(measurable.slot) {
                    Slot.BOTTOM, Slot.TOP -> horizAxisonstraint
                    else -> vertAxisConstraint
                }
                val placeable = measurable.measure(axisConstraint)
                placeables[index] = placeable
            } else { // TODO - separate plot from random non-slot children?
                val placeable = measurable.measure(plotAreaConstraints)
                plotWidth = max(plotWidth, placeable.width)
                plotHeight = max(plotHeight, placeable.height)
                placeables[index] = placeable
                // positions[index] = IntOffset(leftReservation, topReservation)
            }
        }

        // alternative for pass 2
        val ps2 = Array(measurables.size) {index ->
            val measurable = measurables[index]
            if (measurable.isAxis) {
                val axisConstraint = when(measurable.slot) {
                    Slot.BOTTOM, Slot.TOP -> horizAxisonstraint
                    else -> vertAxisConstraint
                }
                measurable.measure(axisConstraint)
            } else { // TODO - separate plot from random non-slot children?
                val placeable = measurable.measure(plotAreaConstraints)
                plotWidth = max(plotWidth, placeable.width)
                plotHeight = max(plotHeight, placeable.height)
                placeable
            }
        }

        val chartWidth = plotWidth + horizontalReservation
        val chartHeight = plotHeight + verticalReservation

        // pass 3 - determine position for each child
        measurables.fastForEachIndexed { index, measurable ->
            if (measurable.isAxis) {
                /*
                 * since axis are allowed to draw outside of their reserved space,
                 * we draw them in most of the chart space with an appropriate alignment.
                 */
                val alignment = measurable.axisAlignment ?: Alignment.Center
                ps2[index]?.let { placeable ->
                    var offset = IntOffset.Zero
                    var space = IntSize.Zero
                    var size = IntSize(placeable.width, placeable.height)
                    measurable.slot?.let {
                        when(it) {
                            Slot.TOP, Slot.BOTTOM -> {
                                offset = IntOffset(leftReservation, 0)
                                space = IntSize(plotWidth, chartHeight)
                                size = IntSize(plotWidth, placeable.height)
                            }
                            Slot.LEFT, Slot.RIGHT -> {
                                offset = IntOffset(0, topReservation)
                                space = IntSize(chartWidth, plotHeight)
                                size = IntSize(placeable.width, plotHeight)
                            }
                            else -> {}
                        }
                    }
                    val position = alignment.align(
                        size,
                        space,
                        LayoutDirection.Ltr
                    ).plus(offset)
                    positions[index] = position
                }
            } else {
                positions[index] = IntOffset(leftReservation, topReservation)
            }
        }

        return layout(chartWidth, chartHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable?.place(positions[index] ?: IntOffset.Zero)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartLayout() {
    ChartLayout(modifier = Modifier.size(50.dp)) {
        Spacer(modifier = Modifier
            .size(10.dp)
            .background(Color.Red)
            .asAxis(slot = Slot.BOTTOM, reserve = Dp.Unspecified))
        Spacer(modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(alpha = 0.5f)))
        Spacer(modifier = Modifier
            .size(5.dp)
            .asAxis(slot = Slot.TOP, reserve = Dp.Unspecified)
            .background(Color.Red))
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartWrap1() {
    ChartLayout(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier
            .size(50.dp)
            .background(Color.Blue))
    }
}


@Preview(showBackground = true)
@Composable
private fun TestChartWrap2() {
    ChartLayout(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier
            .size(50.dp)
            .background(Color.Blue))
        Spacer(modifier = Modifier
            .size(10.dp)
            .background(Color.Red)
            .asAxis(Slot.BOTTOM))
    }
}


@Preview(showBackground = true)
@Composable
private fun TestChartWrap4() {
    ChartLayout(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier
            .size(50.dp)
            .background(Color.Blue))
        Spacer(modifier = Modifier
            .height(10.dp)
            .fillMaxWidth()
            .background(Color.Red)
            .asAxis(Slot.BOTTOM))
        Spacer(modifier = Modifier
            .height(10.dp)
            .fillMaxWidth()
            .background(Color.Red)
            .asAxis(Slot.TOP))
        Spacer(modifier = Modifier
            .width(5.dp)
            .fillMaxHeight()
            .background(Color.Red)
            .asAxis(Slot.LEFT))
        Spacer(modifier = Modifier
            .width(5.dp)
            .fillMaxHeight()
            .background(Color.Red)
            .asAxis(Slot.RIGHT))
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartInBox() {
    Box(Modifier.size(75.dp)) {
        ChartLayout(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue))
            Spacer(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red)
                    .asAxis(Slot.BOTTOM)
            )
        }
    }
}


private val Measurable.chartChildDataNode: ChartChildDataNode? get() = parentData as? ChartChildDataNode
private val Measurable.isAxis: Boolean get() = chartChildDataNode?.isAxis ?: false
private val Measurable.slot: Slot? get() = chartChildDataNode?.slot
private val Measurable.axisAlignment: Alignment? get() = chartChildDataNode?.slot?.let {
    when(it) {
        Slot.LEFT -> Alignment.TopStart
        Slot.RIGHT -> Alignment.TopEnd
        Slot.TOP -> Alignment.TopStart
        Slot.BOTTOM -> Alignment.BottomStart
        else -> null
    }
}

private class ChartChildDataElement(
    val slot: Slot,
    val isAxis: Boolean,
    val reserved: Int,
    val inspectorInfo: InspectorInfo.() -> Unit

) : ModifierNodeElement<ChartChildDataNode>() {
    override fun create(): ChartChildDataNode {
        return ChartChildDataNode(slot, isAxis, reserved)
    }

    override fun update(node: ChartChildDataNode) {
        node.slot = slot
        node.isAxis = isAxis
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun hashCode(): Int {
        var result = slot.hashCode()
        result = 31 * result + isAxis.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? ChartChildDataElement ?: return false
        return slot == otherModifier.slot && reserved == otherModifier.reserved
    }
}

private class ChartChildDataNode(
    var slot: Slot,
    var isAxis: Boolean,
    var reserved: Int,
) : ParentDataModifierNode, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@ChartChildDataNode
}

@Composable
fun ChartLayout(
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

/****************************************
 * end new chart layout
 ****************************************/
@Composable
fun Chart2(
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
    Layout(layoutContent, modifier) {measurables, constraints ->
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            measurable.measure(contentConstraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place children in the parent layout
            placeables.forEach { placeable ->
                // Position item on the screen
                val position = Alignment.BottomStart.align(
                    IntSize(placeable.width, placeable.height),
                    IntSize(constraints.maxWidth, constraints.maxHeight),
                    LayoutDirection.Ltr
                )
                //placeable.placeRelative(x = 0, y = 0)
                placeable.place(position)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun PreviewChart2() {
    Chart2(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)

            drawEach(
                listOf(
                    Offset(25f, 25f),
                    Offset(0f, 0f),
                    Offset(100f, 100f),
                )
            ) {
                drawCircle(Color.Blue, 6.dp.value, it)
            }
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .background(Color.Red))
    }
}
