package com.sanbeg.composable_chart.core

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isFinite
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScaleScope
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.dataSetOf
import com.sanbeg.composable_chart_data.forEach
import com.sanbeg.composable_chart_data.forEachIndexed
import com.sanbeg.composable_chart_data.asDataSet

fun ComposableChartScaleScope.drawEach(
    offsets: DataSet,
    content: DrawScope.(offset: Offset) -> Unit
) {
    offsets.forEach { offset ->
        drawScope.content(scale(offset))
    }
}


fun ComposableChartScaleScope.drawEachFinite(
    offsets: DataSet,
    content: DrawScope.(offset: Offset) -> Unit
) {
    offsets.forEach { offset ->
        if (offset.isSpecified && offset.isFinite) {
            drawScope.content(scale(offset))
        }
    }
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

fun ComposableChartScaleScope.drawAtIndexed(
    offsets: DataSet,
    content: DrawScope.(index: Int) -> Unit
) {
    offsets.forEachIndexed { index, offset ->
        if (offset.isFinite) {
            val scaled = scale(offset)
            drawScope.translate(scaled.x, scaled.y) {
                content(index)
            }
        }
    }
}

fun ComposableChartScaleScope.drawEachSegment(
    offsets: DataSet,
    content: DrawScope.(a: Offset, b: Offset) -> Unit
) {
   var prev = Offset.Unspecified
    offsets.forEach { raw ->
        if (raw.isSpecified) {
            val cur = scale(raw)
            if (prev.isSpecified) {
                drawScope.content(prev, cur)
            }
            prev = cur
        } else {
            prev = raw
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChart() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(
                listOf(
                    Offset(25f, 25f),
                    Offset(0f, 0f),
                    Offset(100f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 4.dp.value, it)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewDrawEachData() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Offset(25f, 25f),
                Offset(0f, 0f),
                Offset(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(dataSet) {
                drawCircle(Color.Blue, 4.dp.value, it)
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
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawAt(dataSet) {
                drawCircle(Color.Blue, 4.dp.value, Offset.Zero)
            }
        }
    }
}