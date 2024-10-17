package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset

private class DataCollectionCollectionView(val data: DataCollection) : AbstractCollection<Offset>() {
    override val size = data.size
    override fun iterator(): Iterator<Offset> = data.iterator()
}

fun DataCollection.asCollection(): Collection<Offset> = DataCollectionCollectionView(this)

private class DataSetListView(val data: StableDataSet) : AbstractList<Offset>() {
    override val size = data.size
    override fun get(index: Int) = data[index]
}

private fun StableDataSet.asList(): List<Offset> = DataSetListView(this)
