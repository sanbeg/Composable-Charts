package com.sanbeg.composable_chart.charts

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScaleScope
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart.core.drawEachSegment
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.dataSetOf

fun ComposableChartScaleScope.line(data: DataSet, width: Dp = Dp.Hairline, brush: Brush) {
    drawEachSegment(data) { a, b ->
        drawLine(brush, a, b, width.toPx(), StrokeCap.Round)
    }
}

// see https://matplotlib.org/stable/gallery/lines_bars_and_markers/stairs_demo.html
enum class StepVertical{
    Pre, Post
}

fun ComposableChartScaleScope.step(
    data: DataSet,
    width: Dp = Dp.Hairline,
    brush: Brush,
    where: StepVertical = StepVertical.Post,
) {
    drawEachSegment(data) { a, c ->
        val px = width.toPx()
        val b = when(where) {
            StepVertical.Pre -> Offset(a.x, c.y)
            StepVertical.Post -> Offset(c.x, a.y)
        }
        drawLine(brush, a, b, px, StrokeCap.Round)
        drawLine(brush, b, c, px, StrokeCap.Round)
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewLine() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Offset(0f, 0f),
                Offset(25f, 20f),
                Offset(45f, 25f),
                Offset(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            line(dataSet, 1.dp, SolidColor(Color.Blue))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStep() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Offset(0f, 0f),
                Offset(25f, 20f),
                Offset(45f, 25f),
                Offset(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            line(dataSet, 1.dp, SolidColor(Color.Cyan))
            step(dataSet, 1.dp, SolidColor(Color.Blue))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewStepPre() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Offset(0f, 0f),
                Offset(25f, 20f),
                Offset(45f, 25f),
                Offset(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            line(dataSet, 1.dp, SolidColor(Color.Cyan))
            step(dataSet, 1.dp, SolidColor(Color.Blue), where=StepVertical.Pre)
        }
    }
}