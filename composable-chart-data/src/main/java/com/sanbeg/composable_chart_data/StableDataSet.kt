package com.sanbeg.composable_chart_data


import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

/**
 * An iterator over an entity that can be represented as a sequence of [Offset]s
 */
interface OffsetIterator : Iterator<Offset> {
    override fun hasNext(): Boolean
    override fun next(): Offset

    /** Returns the next offset in the sequence without boxing.*/
    fun nextOffset(): Offset
}

class DataSetIterator(private val data: StableDataSet) : OffsetIterator {
    private var index = 0

    override fun hasNext() = index < data.size
    override fun next() = data[index++]
    override fun nextOffset() = data[index++]
}

/**
 * An ordered collection of offsets.
 * Methods in this interface support read-only access to the collection.
 *
 * @see dataSetOf
 * @see toDataSet
 */
@Stable
interface StableDataSet {
    /** Returns the size of the collection. */
    val size: Int

    /** Returns the offset at the given index.  This method can be called using the index operator. */
    operator fun get(index: Int): Offset

    /** Returns an iterator over the Offsets in this object.*/
    fun iterator(): OffsetIterator
}

@Stable
val StableDataSet.indices: IntRange
    get() = 0 until size

@Stable
val StableDataSet.lastIndex: Int
    get() = size - 1

fun StableDataSet.forEach(action: (Offset) -> Unit) = indices.forEach {
    action(get(it))
}


fun StableDataSet.onEach(action: (Offset) -> Unit): StableDataSet {
    forEach(action)
    return this
}

fun StableDataSet.forEachIndexed(action: (index: Int, Offset) -> Unit) =
    indices.forEachIndexed { i, o ->
        action(i, get(o))
    }

@Deprecated("use asList().map()")
fun StableDataSet.map(transform: (Offset) -> Offset): List<Offset> =
    mapStableDataSet(this, transform)

private fun mapStableDataSet(data: StableDataSet, transform: (Offset) -> Offset): List<Offset> =
    buildList {
        data.forEach { add(transform(it)) }
    }

fun StableDataSet.firstOrNull() = if (size > 0) this[0] else null
fun StableDataSet.lastOrNull() = if (size > 0) this[size - 1] else null

fun StableDataSet.firstOrNull(predicate: (Offset) -> Boolean): Offset? {
    indices.forEach {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

fun StableDataSet.lastOrNull(predicate: (Offset) -> Boolean): Offset? {
    indices.reversed().forEach {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}
