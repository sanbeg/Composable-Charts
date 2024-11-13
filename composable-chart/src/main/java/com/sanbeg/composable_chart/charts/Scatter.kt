package com.sanbeg.composable_chart.charts

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
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


@JvmInline
private value class OriginCenteredDrawScope(
    private val delegate: DrawScope
) : DrawScope by delegate {
    override val center: Offset
        get() = Offset.Zero
}

fun ComposableChartScaleScope.drawAt(
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

fun ComposableChartScaleScope.scatter(offsets: DataSet, content: DrawScope.() -> Unit) {
    val scope = OriginCenteredDrawScope(drawScope)
    offsets.forEach { offset ->
        if (offset.isSpecified && offset.isFinite) {
            val scaled = scale(offset)
            scope.translate(scaled.x, scaled.y, content)
        }
    }
}

fun ComposableChartScaleScope.scatter(data: DataSet, radius: Dp, brush: Brush) {
    drawAt(data) {
        drawCircle(brush, radius.toPx(), Offset.Zero)
    }
}

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
            scatter(dataSet, 3.dp, SolidColor(Color.Blue))
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