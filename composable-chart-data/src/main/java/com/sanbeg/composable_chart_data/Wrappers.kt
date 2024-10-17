package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset

private class WrapperIterator(val iterator: Iterator<Offset>) : OffsetIterator {
    override fun hasNext() = iterator.hasNext()
    override fun next() = iterator.next()
    override fun nextOffset() = iterator.next()

}
private class ListWrapper(val list: List<Offset>) : StableDataSet {
    override val size = list.size
    override fun iterator(): OffsetIterator = WrapperIterator(list.iterator())
    override fun get(index: Int) = list[index]
}

private class CollectionWrapper(val collection: Collection<Offset>) : DataCollection {
    override val size = collection.size
    override fun iterator(): OffsetIterator = WrapperIterator(collection.iterator())
}

fun dataSetOf(list: List<Offset>): StableDataSet = ListWrapper(list)
fun dataCollectionOf(collection: Collection<Offset>): DataCollection = CollectionWrapper(collection)