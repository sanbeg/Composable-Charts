package com.sanbeg.composable_chart_data


import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

/**
 * An [Iterator] over an entity that can be represented as a sequence of [Offset]s
 */
interface OffsetIterator : Iterator<Offset> {
    override fun hasNext(): Boolean
    override fun next(): Offset

    /** Returns the next [Offset] in the sequence without boxing.*/
    fun nextOffset(): Offset
}

class DataSetIterator(private val data: DataSet) : OffsetIterator {
    private var index = 0

    override fun hasNext() = index < data.size
    override fun next() = data[index++]
    override fun nextOffset() = data[index++]
}

/**
 * An ordered collection of [Offset]s.
 * Methods in this interface support read-only access to the collection.
 *
 * @see dataSetOf
 * @see asDataSet
 */
@Stable
interface DataSet {
    /** Returns the size of the collection. */
    val size: Int

    /** Returns the offset at the given index.  This method can be called using the index operator. */
    operator fun get(index: Int): Offset

    /** Returns an iterator over the Offsets in this object.*/
    fun iterator(): OffsetIterator = DataSetIterator(this)
}

@Stable
val DataSet.indices: IntRange
    get() = 0 until size

@Stable
val DataSet.lastIndex: Int
    get() = size - 1

fun DataSet.forEach(action: (Offset) -> Unit) = indices.forEach {
    action(get(it))
}

fun DataSet.onEach(action: (Offset) -> Unit): DataSet {
    forEach(action)
    return this
}

fun DataSet.forEachIndexed(action: (index: Int, Offset) -> Unit) =
    indices.forEachIndexed { i, o ->
        action(i, get(o))
    }

@Deprecated("use asList().map()")
fun DataSet.map(transform: (Offset) -> Offset): List<Offset> =
    mapStableDataSet(this, transform)

private fun mapStableDataSet(data: DataSet, transform: (Offset) -> Offset): List<Offset> =
    buildList {
        data.forEach { add(transform(it)) }
    }

fun DataSet.first() = this[0]
fun DataSet.last() = this[lastIndex]

fun DataSet.firstOrNull() = if (size > 0) this[0] else null
fun DataSet.lastOrNull() = if (size > 0) this[size - 1] else null

fun DataSet.firstOrNull(predicate: (Offset) -> Boolean): Offset? {
    indices.forEach {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

fun DataSet.lastOrNull(predicate: (Offset) -> Boolean): Offset? {
    indices.reversed().forEach {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

fun <R>DataSet.fold(initial: R, operation: (acc: R, Offset) -> R): R {
    var acc = initial
    for (i in indices) {
        acc = operation(acc, get(i))
    }
    return acc
}