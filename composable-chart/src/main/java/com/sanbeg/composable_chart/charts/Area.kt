package com.sanbeg.composable_chart.charts

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.ComposableChartScaleScope
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.dataSetOf

fun ComposableChartScaleScope.area(data: DataSet, content: DrawScope.(path: Path) -> Unit) {
    var prev: Offset? = null
    val path = Path()
    val height = drawScope.size.height
    drawEach(data) {
        if (prev == null) {
            path.moveTo(it.x, height)
        }
        if (it.isSpecified) {
            path.lineTo(it.x, it.y)
            prev = it
        } else {
            prev?.let { p ->
                path.lineTo(p.x, height)
                path.close()
            }
            prev = null
        }
    }
    prev?.let { p ->
        path.lineTo(p.x, height)
        path.close()
    }
    drawScope.content(path)
}

fun ComposableChartScaleScope.area(data: DataSet, brush: Brush) {
    area(data) {path ->
        drawScope.drawPath(path, brush)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewArea() {
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
            area(dataSet, SolidColor(Color.Blue))
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewAreaGap() {
    Chart(maxX = 100f, dataInset = 6.dp, modifier = Modifier.size(100.dp)) {
        val dataSet = dataSetOf(
            listOf(
                Offset(0f, 0f),
                Offset(25f, 20f),
                Offset.Unspecified,
                Offset(45f, 25f),
                Offset(100f, 100f),
            )
        )
        Scale(maxY = 100f) {
            area(dataSet, SolidColor(Color.Blue))
        }
    }
}