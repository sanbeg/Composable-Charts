package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class DataSetTest {
    private val array = Array(10) {
        Offset(it.toFloat(), it.toFloat())
    }

    private val dataSet = array.asDataSet()

    @Test
    fun testSizeExtensions() {
        assertEquals(array.size, dataSet.size)
        assertEquals(array.lastIndex, dataSet.lastIndex)
        assertEquals(array.indices, dataSet.indices)
    }

    @Test
    fun testForEachIndexed() {
        dataSet.forEachIndexed{i, o ->
            assertEquals(array[i], o)
        }
    }

    @Test
    fun testForEach() {
        var i = 0
        dataSet.forEach{o ->
            assertEquals(array[i++], o)
        }
    }

    @Test
    fun testOnEach() {
        var i = 0
        val self = dataSet.onEach{o ->
            assertEquals(array[i++], o)
        }
        assertEquals(dataSet, self)
    }

    @Test
    fun testFirstLast() {
        assertEquals(array.first(), dataSet.first())
        assertEquals(array.last(), dataSet.last())
        assertEquals(array.first(), dataSet.firstOrNull())
        assertEquals(array.last(), dataSet.lastOrNull())

        // assertEquals(Offset(6f, 6f), dataSet.firstOrNull { it.x > 5f })
    }
}