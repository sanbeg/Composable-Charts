package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import com.sanbeg.composable_chart_data.geometry.Point

@Stable
class SequentialDataSet internal constructor(private val start: Int = 0, private val data: FloatArray) : DataSet {
    override val size get() = data.size
    override fun get(index: Int) = Point(index.plus(start).toFloat(), data[index])
    operator fun plus(other: Float) = SequentialDataSet(start, data + other)
}

fun sequentialDataSetOf(start: Int = 0, vararg floats: Float) = SequentialDataSet(start, floats)