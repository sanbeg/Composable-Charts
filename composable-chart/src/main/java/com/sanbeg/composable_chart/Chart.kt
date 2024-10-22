package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
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

class ComposableChartScope internal constructor(
    internal val minX: Float,
    internal val maxX: Float,
    ) {
    internal var dataInset: Float = 0f

    @Composable
    @Stable
    fun Modifier.asAxis(
        alignment: Alignment = Alignment.BottomCenter,
        reserve: Dp = Dp.Unspecified,
    ) : Modifier = this.then(ChartChildDataElement(
        alignment = alignment,
        isAxis = true,
        reserved = if (reserve.isSpecified) {
            with(LocalDensity.current) { reserve.roundToPx() }
        } else {
            -1
        },
        inspectorInfo = debugInspectorInfo {
            name = "asAxis"
            value = alignment
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
) {
    Box(modifier) {
        ComposableChartScope(minX, maxX).also {
            it.dataInset = dataInset.value
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
            return layout(
                constraints.minWidth,
                constraints.minHeight
            ) {}
        }
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        if (measurables.size == 1) {
            val measurable = measurables[0]
            var boxWidth: Int = constraints.minWidth
            var boxHeight: Int = constraints.minHeight
            //val placeable: Placeable = measurable.measure(Constraints.fixed(constraints.minWidth, constraints.minHeight))
            val placeable = measurable.measure(contentConstraints)
            val alignment = measurable.axisAlignment ?: Alignment.Center
            val position = alignment.align(
                IntSize(placeable.width, placeable.height),
                IntSize(constraints.maxWidth, constraints.maxHeight),
                LayoutDirection.Ltr
            )
            boxWidth = max(boxWidth, placeable.width)
            boxHeight = max(boxHeight, placeable.height)
                //placeable.placeRelative(x = 0, y = 0)
            //return layout(constraints.minWidth, constraints.minHeight) {
            return layout(boxWidth, boxHeight) {
                placeable.place(position)
            }

        }

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        val positions = arrayOfNulls<IntOffset>(measurables.size)

        var topReservation = 0
        var bottomReservation = 0
        var boxWidth: Int = constraints.minWidth
        var boxHeight: Int = constraints.minHeight
        measurables.fastForEachIndexed{ index, measurable ->
            if (measurable.isAxis) {
                val placeable = measurable.measure(contentConstraints)

                var reserve = measurable.chartChildDataNode?.reserved ?: 0
                if (reserve < 0) {
                    reserve = placeable.height
                }

                val alignment = measurable.axisAlignment ?: Alignment.Center

                if (((alignment as? BiasAlignment)?.verticalBias ?: 0f) < 0f) {
                    topReservation = max(topReservation, reserve)
                } else {
                    bottomReservation = max(bottomReservation, reserve)
                }
                val position = alignment.align(
                    IntSize(placeable.width, placeable.height),
                    IntSize(constraints.maxWidth, constraints.maxHeight),
                    LayoutDirection.Ltr
                )
                placeables[index] = placeable
                positions[index] = position
            }
        }

        val totalReservation = topReservation + bottomReservation

        val chartAreaConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxHeight = constraints.maxHeight - totalReservation
        )

        measurables.fastForEachIndexed { index, measurable ->
            if (! measurable.isAxis) {
                val placeable = measurable.measure(chartAreaConstraints)
                val alignment = measurable.axisAlignment ?: Alignment.TopStart
                boxWidth = max(boxWidth, placeable.width)
                boxHeight = max(boxHeight, placeable.height)
                val position = alignment.align(
                    IntSize(placeable.width, placeable.height),
                    IntSize(constraints.maxWidth, constraints.maxHeight - totalReservation),
                    LayoutDirection.Ltr
                ).plus(IntOffset(0, topReservation))
                placeables[index] = placeable
                positions[index] = position
            }
        }

        val chartWidth = constraints.minWidth.takeIf { it > 0 } ?: boxWidth
        val chartHeight = constraints.minHeight.takeIf { it > 0 } ?: (boxHeight + totalReservation)

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
            .asAxis(reserve = Dp.Unspecified))
        Spacer(modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(alpha = 0.5f)))
        Spacer(modifier = Modifier
            .size(5.dp)
            .asAxis(alignment = Alignment.TopStart, reserve = Dp.Unspecified)
            .background(Color.Red))
    }
}

@Preview(showBackground = true)
@Composable
private fun TestChartWrap1() {
    ChartLayout(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier.size(50.dp).background(Color.Blue))
    }
}


@Preview(showBackground = true)
@Composable
private fun TestChartWrap2() {
    ChartLayout(modifier = Modifier.wrapContentSize()) {
        Spacer(modifier = Modifier.size(50.dp).background(Color.Blue))
        Spacer(modifier = Modifier.size(10.dp).background(Color.Red).asAxis(Alignment.BottomCenter))
    }
}




@Preview(showBackground = true)
@Composable
private fun TestChartInBox() {
    Box(Modifier.size(75.dp)) {
        ChartLayout(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.fillMaxSize().background(Color.Blue))
            Spacer(
                modifier = Modifier.size(10.dp).background(Color.Red).asAxis(Alignment.BottomCenter)
            )
        }
    }
}


private val Measurable.chartChildDataNode: ChartChildDataNode? get() = parentData as? ChartChildDataNode
private val Measurable.isAxis: Boolean get() = chartChildDataNode?.isAxis ?: false
private val Measurable.axisAlignment: Alignment? get() = chartChildDataNode?.alignment

private class ChartChildDataElement(
    val alignment: Alignment,
    //val matchParentSize: Boolean,
    val isAxis: Boolean,
    val reserved: Int,
    val inspectorInfo: InspectorInfo.() -> Unit

) : ModifierNodeElement<ChartChildDataNode>() {
    override fun create(): ChartChildDataNode {
        //return ChartChildDataNode(alignment, matchParentSize)
        return ChartChildDataNode(alignment, isAxis, reserved)
    }

    override fun update(node: ChartChildDataNode) {
        node.alignment = alignment
        //node.matchParentSize = matchParentSize
        node.isAxis = isAxis
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun hashCode(): Int {
        var result = alignment.hashCode()
        //result = 31 * result + matchParentSize.hashCode()
        result = 31 * result + isAxis.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? ChartChildDataElement ?: return false
        return alignment == otherModifier.alignment &&
                //matchParentSize == otherModifier.matchParentSize
                isAxis == otherModifier.isAxis
    }
}

private class ChartChildDataNode(
    var alignment: Alignment,
    //var matchParentSize: Boolean,
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
            it.dataInset = dataInset.value
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
            it.dataInset = dataInset.value
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
