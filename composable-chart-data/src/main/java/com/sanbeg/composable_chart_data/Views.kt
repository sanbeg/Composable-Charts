package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset

private class DataSetListView(val data: StableDataSet) : AbstractList<Offset>() {
    override val size
        get() = data.size

    override fun get(index: Int) = data[index]
}

fun StableDataSet.asList(): List<Offset> = DataSetListView(this)
