package com.sanbeg.composable_chart_data

import com.sanbeg.composable_chart_data.geometry.Point

@JvmInline
private value class ListWrapper(val list: List<Point>) : DataSet {
    override val size
        get() = list.size

    override fun get(index: Int) = list[index]
}

@JvmInline
private value class ArrayWrapper(val array: Array<Point>) : DataSet {
    override val size: Int
        get() = array.size

    override fun get(index: Int) = array[index]
}

// maybe remove these?
/** Creates a [DataSet] as a wrapped List of boxed [Point]s, like [list.asDataSet()] */
@Deprecated("replaced with extension", ReplaceWith("list.asDataSet()", "com.sanbeg.composable_chart_data.asDataSet"))
fun dataSetOf(list: List<Point>): DataSet = ListWrapper(list)
/** Creates a [DataSet] as a wrapped Array of boxed [Point]s, like [array.asDataSet()] */
@Deprecated("replaced with extension", ReplaceWith("array.asDataSet()", "com.sanbeg.composable_chart_data.asDataSet"))
fun dataSetOf(array: Array<Point>): DataSet = ArrayWrapper(array)

/** Creates a [DataSet] as a wrapped List of boxed [Point]s. */
fun List<Point>.asDataSet(): DataSet = ListWrapper(this)
/** Creates a [DataSet] as a wrapped Array of boxed [Point]s. */
fun Array<Point>.asDataSet(): DataSet = ArrayWrapper(this)

/** Creates a [DataSet] as a wrapped [FloatArray], without boxing. */
fun FloatArray.asDataSet(): DataSet = FloatArrayDataSet(this)

/**
 * Creates a [DataSet] as a wrapped [FloatArray], without boxing.
 * @param[elements] alternating x and y elements; the number of arguments must be even.
 */
fun dataSetOf(vararg elements: Float): FloatArrayDataSet = FloatArrayDataSet(elements)

fun emptyDataSet(): DataSet = object: DataSet {
    override val size = 0
    override fun get(index: Int): Point {
        return emptyArray<Point>()[index]
    }
}