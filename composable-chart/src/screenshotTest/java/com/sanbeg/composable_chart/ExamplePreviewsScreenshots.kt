package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.axis.BottomAxis
import com.sanbeg.composable_chart.axis.LeftAxis
import com.sanbeg.composable_chart.axis.drawLabelledTics
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart.plots.line
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.xRange
import com.sanbeg.composable_chart_data.yRange

class ExamplePreviewsScreenshots {

    private val chartData = listOf(
        Point(0f, 0f),
        Point(10f, 10f),
        Point(15f, 7f),
        Point(25f, 30f),
        Point(40f, 40f),
        Point(50f, 50f)
    ).asDataSet()

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Chart(
            Modifier
                .size(150.dp)
                .xRange(chartData.xRange())
                .yRange(chartData.yRange())
        ) {
            Plot {
                line(chartData)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AxisPreview() {
        Chart(
            Modifier
                .size(150.dp)
                .xRange(chartData.xRange())
                .yRange(chartData.yRange())
        ) {
            Plot {
                line(chartData)
            }
            val measurer = rememberTextMeasurer()
            LeftAxis(Modifier.width(12.dp)) {
                drawLabelledTics(spacing = 10f, textMeasurer = measurer,)
            }
            BottomAxis(Modifier.height(12.dp)) {
                drawLabelledTics(10f, measurer, format = "%.0f")
            }
        }
    }

}