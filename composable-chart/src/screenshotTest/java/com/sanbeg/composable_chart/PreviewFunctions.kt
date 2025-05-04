package com.sanbeg.composable_chart

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.xRange
import com.sanbeg.composable_chart.core.yRange
import com.sanbeg.composable_chart.plots.Function
import com.sanbeg.composable_chart.plots.line
import kotlin.math.PI
import kotlin.math.sin

class PreviewFunctions {
    @Preview(showBackground = true)
    @Composable
    private fun PreviewFunctionResolution() {
        Chart(
            modifier = Modifier
                .size(150.dp)
                .yRange(-1f, 1f)
                .xRange(0f, 100f)
        ) {
            Function(resolution = 5.dp) {
                line { x -> sin(x / PI.toFloat().times(2)) }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    private fun PreviewSinFunction() {
        Chart(
            modifier = Modifier
                .size(150.dp)
                .yRange(-1f, 1f)
                .xRange(0f, 100f)
        ) {
            Function() {
                line { x -> sin(x / PI.toFloat().times(2)) }
            }
        }
    }

}