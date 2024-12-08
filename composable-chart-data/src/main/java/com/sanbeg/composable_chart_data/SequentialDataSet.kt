package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import com.sanbeg.composable_chart_data.geometry.Point

@Stable
@JvmInline
value class SequentialDataSet(private val data: FloatArray) : DataSet {
    override val size get() = data.size
    override fun get(index: Int) = Point(index.toFloat(), data[index])
    operator fun plus(other: Float) = SequentialDataSet(data + other)
}

fun sequentialDataSetOf(vararg floats: Float) = SequentialDataSet(floats)