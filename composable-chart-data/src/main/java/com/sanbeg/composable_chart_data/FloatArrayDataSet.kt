package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import com.sanbeg.composable_chart_data.function.IntToPointFunction
import com.sanbeg.composable_chart_data.point.Point
import com.sanbeg.composable_chart_data.point.isSpecified

private fun Point.copyIntoArray(dst: FloatArray, start: Int) = if (isSpecified) {
    dst[start] = x
    dst[start+1] = y
} else {
    dst[start] = Float.NaN
    dst[start+1] = Float.NaN
}

@JvmInline
@Stable
value class FloatArrayDataSet(private val array: FloatArray) : DataSet {
    init {
        require(array.size % 2 == 0) {
            "Array must have an even number of elements"
        }
    }

    constructor(size: Int, block: IntToPointFunction) : this(
        FloatArray(size).apply {
            for (i in indices) {
                block(i).copyIntoArray(this, i * 2)
            }
        }
    )

    constructor(x: FloatArray, y:FloatArray) : this(
        require(x.size == y.size) { "x and y must have equal size" }.let {
            FloatArray(x.size * 2 ).apply {
                x.forEachIndexed { index, fl ->
                    this[index * 2] = fl
                }
                y.forEachIndexed { index, fl ->
                    this[index * 2 + 1] = fl
                }
            }
        }
    )

    override val size get() = array.size / 2

    override fun get(index: Int) = Point(array[index * 2], array[index * 2 + 1])

    operator fun plus(offset: Point): FloatArrayDataSet {
        val rv = FloatArray(array.size + 2)
        array.copyInto(rv)
        offset.copyIntoArray(rv, array.size)
        return FloatArrayDataSet(rv)
    }

    operator fun plus(other: FloatArrayDataSet) = FloatArrayDataSet(array + other.array)

    operator fun plus(other: FloatArray) = FloatArrayDataSet(array + other)
}