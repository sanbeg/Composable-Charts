package com.sanbeg.composable_chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.geometry.Point

class ExamplePreviewsScreenshots {

    val chartData = listOf(
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

    }


}