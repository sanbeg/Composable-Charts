package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified

private fun Offset.copyIntoArray(dst: FloatArray, start: Int) = if (isSpecified) {
    dst[start] = x
    dst[start+1] = y
} else {
    dst[start] = Float.NaN
    dst[start+1] = Float.NaN
}

@JvmInline
@Stable
value class FloatArrayDataSet(private val array: FloatArray) : StableDataSet {
    private class FloatArrayDataSetIterator(val array: FloatArray): OffsetIterator{
        private var nextIndex = 0

        override fun hasNext(): Boolean = nextIndex < array.size

        override fun next() = nextOffset()

        override fun nextOffset() = Offset(array[nextIndex++], array[nextIndex++])
    }

    init {
        require(array.size % 2 == 0) {
            "Array must have an even number of elements"
        }
    }

    constructor(size: Int, block: (Int) -> Offset) : this(
        FloatArray(size).apply {
            indices.forEach { i ->
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

    override fun get(index: Int) = Offset(array[index * 2], array[index * 2 + 1])

    override fun iterator(): OffsetIterator = FloatArrayDataSetIterator(array)

    operator fun plus(offset: Offset) {
        val rv = FloatArray(array.size + 2)
        array.copyInto(rv)
        offset.copyIntoArray(rv, array.size)
    }

    operator fun plus(other: FloatArrayDataSet) = FloatArrayDataSet(array + other.array)

    operator fun plus(other: FloatArray) = FloatArrayDataSet(array + other)
}