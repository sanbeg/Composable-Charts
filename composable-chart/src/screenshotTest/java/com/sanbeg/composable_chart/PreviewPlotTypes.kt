package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.plotInset
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart.plots.StepVertical
import com.sanbeg.composable_chart.plots.area
import com.sanbeg.composable_chart.plots.line
import com.sanbeg.composable_chart.plots.scatter
import com.sanbeg.composable_chart.plots.step
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.xRange
import com.sanbeg.composable_chart_data.yRange

private typealias PlotType = PlotScope.(DataSet) -> Unit

private class PlotProvider: PreviewParameterProvider<PlotScope.(DataSet) -> Unit> {
    override val values: Sequence<PlotScope.(DataSet) -> Unit> = sequenceOf(
        { line(it, Color.Black) },
        { scatter(it, 1.dp, Color.Black) },
        { area(it, SolidColor(Color.Cyan)) },
        {
            area(it, SolidColor(Color.Cyan))
            line(it, Color.Blue)
            scatter(it, 1.dp, Color.Black)
        },
        {
            step(it, color = Color.Blue)
            scatter(it, 1.dp, Color.Black)
        },
        {
            step(it, color = Color.Blue, where = StepVertical.Pre)
            scatter(it, 1.dp, Color.Black)
        },
    )

}

class PreviewPlotTypes {
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
    fun PlotPreview(@PreviewParameter(PlotProvider::class) plot: PlotType ) {
        Chart(
            Modifier
                .size(150.dp)
                .xRange(chartData.xRange())
                .yRange(chartData.yRange())
                .plotInset(1.dp)

        ) {
            Plot {
                plot(chartData)
            }
        }
    }

}

