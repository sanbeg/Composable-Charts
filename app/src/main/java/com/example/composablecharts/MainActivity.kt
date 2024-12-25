package com.example.composablecharts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composablecharts.ui.theme.ComposableChartsTheme
import com.sanbeg.composable_chart.Chart
import com.sanbeg.composable_chart.Scale
import com.sanbeg.composable_chart.charts.fastArea
import com.sanbeg.composable_chart.charts.line
import com.sanbeg.composable_chart_data.DataSet
import com.sanbeg.composable_chart_data.ImmutableDataSet
import com.sanbeg.composable_chart_data.geometry.Point
import kotlin.math.sin

data class ViewModel(
    val maxX: Float,
    val minY: Float,
    val maxY: Float,

    val data: DataSet
)

val data = ImmutableDataSet(10000) { i ->
    val fi = i.toFloat() / 100f
    Point(fi, sin(fi))
}

val model = ViewModel(maxX = 100f, minY = -1f, maxY = 1f, data = data)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            ComposableChartsTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        model = model,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, model: ViewModel, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        var slideval by remember { mutableFloatStateOf(model.maxX) }
        Slider(value = slideval, valueRange = 0f .. model.maxX, modifier = Modifier.fillMaxWidth(), onValueChange = {slideval = it})
        Chart(minX = 0f, maxX = slideval, modifier = Modifier.height(150.dp).fillMaxWidth()) {
            Scale(minY = model.minY, maxY = model.maxY) {
                line(model.data, brush = SolidColor(Color.Black))
                fastArea(model.data, brush = SolidColor(Color.Cyan))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposableChartsTheme {
        Greeting("Android", model)
    }
}