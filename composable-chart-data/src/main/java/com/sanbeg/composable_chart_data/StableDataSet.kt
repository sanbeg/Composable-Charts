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

/**
 * A possibly unordered collection of [Offset]s.
 * Methods in this interface support read-only access to the collection.
 *
 * @see dataCollectionOf
 */
@Stable
interface DataCollection {
    /** Returns the size of the collection.*/
    val size: Int

    /** Returns an iterator over the Offsets in this object.*/
    fun iterator(): OffsetIterator
}

/**
 * An ordered collection of offsets.
 * Methods in this interface support read-only access to the collection.
 *
 * @see dataSetOf
 */
@Stable
interface StableDataSet : DataCollection {
    /** Returns the size of the collection. */
    override val size: Int

    /** Returns the offset at the given index.  This method can be called using the index operator. */
    operator fun get(index: Int): Offset

    /** Returns an iterator over the Offsets in this object.*/
    override fun iterator(): OffsetIterator
}

@Stable
val DataCollection.indices: IntRange
    get() = 0 until size

@Stable
val DataCollection.lastIndex: Int
    get() = size - 1

fun StableDataSet.forEach(action: (Offset) -> Unit) = indices.forEach {
    action(get(it))
}

fun DataCollection.forEach(action: (Offset) -> Unit) {
    for (offset in iterator()) {
        action(offset)
    }
}

fun DataCollection.onEach(action: (Offset) -> Unit): DataCollection {
    forEach(action)
    return this
}

fun StableDataSet.forEachIndexed(action: (index: Int, Offset) -> Unit) =
    indices.forEachIndexed { i, o ->
        action(i, get(o))
    }

fun StableDataSet.toImmutableDataSet(): ImmutableDataSet = if (this is ImmutableDataSet) {
    this
} else {
    ImmutableDataSet(size) { i ->
        this[i]
    }
}

fun StableDataSet.plus(other: StableDataSet) = ImmutableDataSet(size + other.size) { i ->
    if (i < size) {
        this[i]
    } else {
        other[i - size]
    }
}

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
