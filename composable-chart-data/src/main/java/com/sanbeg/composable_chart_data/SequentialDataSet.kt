package com.sanbeg.composable_chart_data

import com.sanbeg.composable_chart_data.point.Point

@JvmInline
value class SequentialDataSet(private val data: FloatArray) : DataSet {
    constructor(vararg floats: Float) : this(floats)

    override val size get() = data.size
    override fun get(index: Int) = Point(index.toFloat(), data[index])
    operator fun plus(other: Float) = SequentialDataSet(data + other)
}