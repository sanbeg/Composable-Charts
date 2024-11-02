package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset

private class DataSetListView(val data: DataSet) : AbstractList<Offset>() {
    override val size
        get() = data.size

    override fun get(index: Int) = data[index]
}

@JvmInline
private value class DataSetIterableView(val data: DataSet) : Iterable<Offset> {
    override fun iterator(): Iterator<Offset> = data.iterator()
}

class DataSetSubset(
    private val data: DataSet,
    private val start: Int = 0,
    override val size: Int = data.size - start
) : DataSet {
    override fun get(index: Int): Offset = data[index + start]
    override fun iterator(): OffsetIterator = DataSetIterator(this)
}

fun DataSet.asList(): List<Offset> = DataSetListView(this)
fun DataSet.asIterable(): Iterable<Offset> = DataSetIterableView(this)
