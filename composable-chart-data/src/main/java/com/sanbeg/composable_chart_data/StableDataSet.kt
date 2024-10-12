package com.sanbeg.composable_chart_data


import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

interface OffsetIterator : Iterator<Offset> {
    override fun hasNext(): Boolean
    override fun next(): Offset
    fun nextOffset(): Offset
}

@Stable
interface StableDataSet {
    val size: Int
    operator fun get(index: Int): Offset
    fun iterator() : OffsetIterator
}

val StableDataSet.indices: IntRange
    get() = 0 until size

val StableDataSet.lastIndex: Int
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