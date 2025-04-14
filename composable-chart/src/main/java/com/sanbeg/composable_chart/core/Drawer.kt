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
import com.sanbeg.composable_chart.Plot
import com.sanbeg.composable_chart.PlotScope
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.forEach
import com.sanbeg.composable_chart_data.forEachIndexed
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.geometry.isFinite
import com.sanbeg.composable_chart_data.geometry.isSpecified

internal inline fun PlotScope.drawEach(
    offsets: DataSet,
    crossinline content: DrawScope.(offset: Offset) -> Unit
) {
    offsets.forEach { offset ->
        drawScope.content(scale(offset))
    }
}

internal inline fun PlotScope.drawEachFinite(
    points: DataSet,
    crossinline content: DrawScope.(offset: Offset) -> Unit
) {
    points.forEach { offset ->
        if (offset.isSpecified && offset.isFinite) {
            drawScope.content(scale(offset))
        }
    }
}

// can replace with scatterWithIndexedValues
private inline fun PlotScope.drawAtIndexed(
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

inline fun PlotScope.drawAtEach(
    points: DataSet,
    crossinline content: DrawScope.(point: Point) -> Unit
) {
    points.forEach {point ->
        if (point.isFinite) {
            val scaled = scale(point)
            drawScope.translate(scaled.x, scaled.y) {
                content(point)
            }
        }
    }
}

inline fun PlotScope.drawEachSegment(
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
    Chart(Modifier.size(100.dp).xRange(0f, 100f).plotInset(6.dp)) {
        Plot(Modifier.yRange(0f, 100f)) {
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
    Chart(modifier = Modifier.size(100.dp).plotInset(6.dp).xRange(0f, 100f)) {
        val dataSet = listOf(
            Point(25f, 25f),
            Point(0f, 0f),
            Point(100f, 100f),
        ).asDataSet()
        Plot(Modifier.yRange(0f, 100f)) {
            // drawScope.drawCircle(Color.Red, 4.dp.value)
            drawEach(dataSet) {
                drawCircle(Color.Blue, 4.dp.value, it)
            }
        }
    }
}
