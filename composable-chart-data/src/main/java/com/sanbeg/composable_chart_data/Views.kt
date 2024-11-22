package com.sanbeg.composable_chart_data


/**
 * An [Iterator] over an entity that can be represented as a sequence of [Point]s
 */
interface PointIterator : Iterator<Point> {
    override fun hasNext(): Boolean
    override fun next(): Point

    /** Returns the next [Point] in the sequence without boxing.*/
    fun nextPoint(): Point
}

private class DataSetIterator(private val data: DataSet) : PointIterator {
    private var index = 0

    override fun hasNext() = index < data.size
    override fun next() = data[index++]
    override fun nextPoint() = data[index++]
}

private class DataSetListView(val data: DataSet) : AbstractList<Point>() {
    override val size
        get() = data.size

    override fun get(index: Int) = data[index]
}

interface PointIterable : Iterable<Point> {
    /** Returns an iterator over the [Point]s in this object.*/
    override fun iterator(): PointIterator
}

@JvmInline
private value class DataSetIterableView(val data: DataSet) : PointIterable {
    override fun iterator(): PointIterator = DataSetIterator(data)
}

private class DataSetSubset(
    private val data: DataSet,
    private val start: Int = 0,
    override val size: Int = data.size - start
) : DataSet {
    override fun get(index: Int): Point = data[index + start]
}

/**
 * Convert this DataSet into a [List] which delegates calls to the underlying dataset.
 * This allows for interoperability when a list is needed and performance isn't critical.
 */
fun DataSet.asList(): List<Point> = DataSetListView(this)

/**
 * Convert this DataSet into an [Iterable].  The returned Iterable view would be implemented
 * as a value class, this doesn't require allocation a new object.  Its iterator provides a
 * nextPoint method which allows it to iterate through the points without boxing.
 */
fun DataSet.asIterable(): PointIterable = DataSetIterableView(this)
fun DataSet.slice(indices: IntRange): DataSet = DataSetSubset(this, indices.first, indices.last - indices.first)
