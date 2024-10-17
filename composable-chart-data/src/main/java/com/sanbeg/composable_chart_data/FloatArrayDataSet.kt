package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset


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

    override val size get() = array.size / 2

    override fun get(index: Int) = Offset(array[index * 2], array[index * 2 + 1])

    override fun iterator(): OffsetIterator = FloatArrayDataSetIterator(array)

    operator fun plus(offset: Offset) {
        val rv = FloatArray(array.size + 2)
        array.copyInto(rv)
        rv[array.size] = offset.x
        rv[array.size + 1] = offset.y
    }

    operator fun plus(other: FloatArrayDataSet) = FloatArrayDataSet(array + other.array)

    operator fun plus(other: FloatArray) = FloatArrayDataSet(array + other)
}