package com.sanbeg.composable_chart.core

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.forEach
import com.sanbeg.composable_chart_data.forEachIndexed
import com.sanbeg.composable_chart_data.point.Point
import com.sanbeg.composable_chart_data.point.isFinite
import com.sanbeg.composable_chart_data.point.isSpecified

inline fun ComposableChartScaleScope.drawEach(
    offsets: DataSet,
    crossinline content: DrawScope.(offset: Offset) -> Unit
) {
    offsets.forEach { offset ->
        drawScope.content(scale(offset))
    }
}


inline fun ComposableChartScaleScope.drawEachFinite(
    offsets: DataSet,
    crossinline content: DrawScope.(offset: Offset) -> Unit
) {
    offsets.forEach { offset ->
        if (offset.isSpecified && offset.isFinite) {
            drawScope.content(scale(offset))
        }
    }
}

inline fun ComposableChartScaleScope.drawAtIndexed(
    offsets: DataSet,
    crossinline content: DrawScope.(index: Int) -> Unit
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

inline fun ComposableChartScaleScope.drawEachSegment(
    offsets: DataSet,
    crossinline content: DrawScope.(a: Offset, b: Offset) -> Unit
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
            prev = Offset.Unspecified
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
                    Point(25f, 25f),
                    Point(0f, 0f),
                    Point(100f, 100f),
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
        val dataSet = listOf(
            Point(25f, 25f),
            Point(0f, 0f),
            Point(100f, 100f),
        ).asDataSet()
        Scale(maxY = 100f) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(dataSet) {
                drawCircle(Color.Blue, 4.dp.value, it)
            }
        }
    }
}
