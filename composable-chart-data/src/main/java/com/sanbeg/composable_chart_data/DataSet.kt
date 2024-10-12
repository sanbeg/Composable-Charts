package com.sanbeg.composable_chart_data


import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

interface OffsetIterator : Iterator<Offset> {
    override fun hasNext(): Boolean
    override fun next(): Offset
    fun nextOffset(): Offset
}

@Stable
interface DataSet {
    val size: Int
    operator fun get(index: Int): Offset
    fun iterator() : OffsetIterator
}

val DataSet.indices: IntRange
    get() = 0 until size

val DataSet.lastIndex: Int
    get() = size -1

fun DataSet.forEach(action: (Offset) -> Unit) = indices.forEach {
    action(get(it))
}

fun DataSet.forEachIndexed(action: (index: Int, Offset) -> Unit) = indices.forEachIndexed {i, o ->
    action(i, get(o))
}