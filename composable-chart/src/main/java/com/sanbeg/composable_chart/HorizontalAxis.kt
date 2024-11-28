package com.sanbeg.composable_chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart.core.drawEach
import com.sanbeg.composable_chart_data.asDataSet
import com.sanbeg.composable_chart_data.point.Point

class HorizontalAxisScope internal constructor(
    private val chartScope: ComposableChartScope,
    internal val drawScope: DrawScope,
    ) {
    private val scale =
        (drawScope.size.width - chartScope.dataInset * 2) / (chartScope.maxX - chartScope.minX)

    fun scale(x: Float): Float = x * scale + chartScope.dataInset

    val minX by chartScope::minX
    val maxX by chartScope::maxX
}

@Composable
fun ComposableChartScope.HorizontalAxis(
    modifier: Modifier,
    edge: Edge = Edge.BOTTOM,
    content: HorizontalAxisScope.() -> Unit
) {
    Spacer(
        modifier
            .fillMaxWidth()
            .asAxis(edge)
            .drawBehind {
                HorizontalAxisScope(
                    this@HorizontalAxis,
                    this
                ).content()
            }
    )
}

fun HorizontalAxisScope.drawAt(x: Float, draw: DrawScope.() -> Unit) {
    drawScope.translate(scale(x)) {
        draw()
    }
}

fun HorizontalAxisScope.drawTics(spacing: Float) {
    var x = minX
    while (x <= maxX) {
        drawAt(x) {
            drawLine(Color.Black, Offset.Zero, Offset(0f, size.height))
        }
        x += spacing
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewChartAxis() {
    Chart(maxX = 100f, dataInset = 3.dp, modifier = Modifier.size(100.dp)) {
        Scale(maxY = 100f, modifier = Modifier) {
            drawEach(
                listOf(
                    Point(25f, 25f),
                    Point(0f, 0f),
                    Point(100f, 100f),
                ).asDataSet()
            ) {
                drawCircle(Color.Blue, 3.dp.toPx(), it)
            }
        }

        HorizontalAxis(Modifier.height(8.dp)) {
            drawTics(10f)
        }
        HorizontalAxis(Modifier.height(4.dp).background(Color.Cyan), edge = Edge.TOP) {
            drawTics(15f)
        }
    }
}