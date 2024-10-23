package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset

@JvmInline
private value class WrapperIterator(val iterator: Iterator<Offset>) : OffsetIterator {
    override fun hasNext() = iterator.hasNext()
    override fun next() = iterator.next()
    override fun nextOffset() = iterator.next()

}
@JvmInline
private value class ListWrapper(val list: List<Offset>) : StableDataSet {
    override val size
        get() = list.size

    override fun get(index: Int) = list[index]
    override fun iterator(): OffsetIterator = WrapperIterator(list.iterator())
}

@JvmInline
private value class ArrayWrapper(val array: Array<Offset>) : StableDataSet {
    override val size: Int
        get() = array.size

    override fun get(index: Int) = array[index]
    override fun iterator(): OffsetIterator = WrapperIterator(array.iterator())
}

// maybe remove these?
/** Creates a StableDataSet as a wrapped List of boxed Offsets. */
fun dataSetOf(list: List<Offset>): StableDataSet = ListWrapper(list)
/** Creates a StableDataSet as a wrapped Array of boxed Offsets. */
fun dataSetOf(array: Array<Offset>): StableDataSet = ArrayWrapper(array)

/** Creates a StableDataSet as a wrapped List of boxed Offsets. */
fun List<Offset>.toDataSet(): StableDataSet = ListWrapper(this)
/** Creates a StableDataSet as a wrapped Array of boxed Offsets. */
fun Array<Offset>.toDataSet(): StableDataSet = ArrayWrapper(this)

/**
 * Creates a StableDataSet as a wrapped FloatArray, without boxing.
 * @param[elements] alternating x and y elements; the number of arguments must be even.
 */
fun dataSetOf(vararg elements: Float): FloatArrayDataSet = FloatArrayDataSet(elements)