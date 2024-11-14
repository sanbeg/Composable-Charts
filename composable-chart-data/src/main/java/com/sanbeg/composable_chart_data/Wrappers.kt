package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset

@JvmInline
private value class WrapperIterator(val iterator: Iterator<Offset>) : OffsetIterator {
    override fun hasNext() = iterator.hasNext()
    override fun next() = iterator.next()
    override fun nextOffset() = iterator.next()

}
@JvmInline
private value class ListWrapper(val list: List<Offset>) : DataSet {
    override val size
        get() = list.size

    override fun get(index: Int) = list[index]
    override fun iterator(): OffsetIterator = WrapperIterator(list.iterator())
}

@JvmInline
private value class ArrayWrapper(val array: Array<Offset>) : DataSet {
    override val size: Int
        get() = array.size

    override fun get(index: Int) = array[index]
    override fun iterator(): OffsetIterator = WrapperIterator(array.iterator())
}

// maybe remove these?
/** Creates a [DataSet] as a wrapped List of boxed [Offset]s, like [list.asDataSet()] */
fun dataSetOf(list: List<Offset>): DataSet = ListWrapper(list)
/** Creates a [DataSet] as a wrapped Array of boxed [Offset]s, like [array.asDataSet()] */
fun dataSetOf(array: Array<Offset>): DataSet = ArrayWrapper(array)

/** Creates a [DataSet] as a wrapped List of boxed [Offset]s. */
fun List<Offset>.asDataSet(): DataSet = ListWrapper(this)
/** Creates a [DataSet] as a wrapped Array of boxed [Offset]s. */
fun Array<Offset>.asDataSet(): DataSet = ArrayWrapper(this)

/** Creates a [DataSet] as a wrapped [FloatArray], without boxing. */
fun FloatArray.asDataSet(): DataSet = FloatArrayDataSet(this)

/**
 * Creates a [DataSet] as a wrapped [FloatArray], without boxing.
 * @param[elements] alternating x and y elements; the number of arguments must be even.
 */
fun dataSetOf(vararg elements: Float): FloatArrayDataSet = FloatArrayDataSet(elements)