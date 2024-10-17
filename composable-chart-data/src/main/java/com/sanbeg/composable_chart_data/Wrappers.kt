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
    override fun iterator(): OffsetIterator = WrapperIterator(list.iterator())
    override fun get(index: Int) = list[index]
}

@JvmInline
private value class CollectionWrapper(val collection: Collection<Offset>) : DataCollection {
    override val size
        get() = collection.size
    override fun iterator(): OffsetIterator = WrapperIterator(collection.iterator())
}

fun dataSetOf(list: List<Offset>): StableDataSet = ListWrapper(list)
fun dataCollectionOf(collection: Collection<Offset>): DataCollection = CollectionWrapper(collection)