package com.sanbeg.composable_chart_data


import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

interface OffsetIterator : Iterator<Offset> {
    override fun hasNext(): Boolean
    override fun next(): Offset
    fun nextOffset(): Offset
}

/**
 * A possibly unordered group of offsets
 */
@Stable
interface DataCollection {
    val size: Int
    fun iterator() : OffsetIterator
}

/**
 * An ordered group of offsets
 */
@Stable
interface StableDataSet: DataCollection {
    override val size: Int
    operator fun get(index: Int): Offset
    override fun iterator() : OffsetIterator
}

val DataCollection.indices: IntRange
    get() = 0 until size

val DataCollection.lastIndex: Int
    get() = size -1

fun StableDataSet.forEach(action: (Offset) -> Unit) = indices.forEach {
    action(get(it))
}

fun StableDataSet.forEachIndexed(action: (index: Int, Offset) -> Unit) = indices.forEachIndexed { i, o ->
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
fun StableDataSet.lastOrNull() = if (size > 0) this[size-1] else null

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
