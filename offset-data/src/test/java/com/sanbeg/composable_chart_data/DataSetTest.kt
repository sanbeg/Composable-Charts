package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DataSetTest {
    private val array = Array(10) {
        Offset(it.toFloat(), it.toFloat())
    }

    private val dataSet = array.asDataSet()

    private val emptySet = emptyArray<Offset>().asDataSet()

    @Test
    fun testDefaultIterator() {
        val emptyFakeDataSet = object : DataSet {
            override val size = 0
            override fun get(index: Int): Offset {
                TODO("Not yet implemented")
            }
        }
        val singleFakeDataSet = object : DataSet {
            override val size = 1
            override fun get(int: Int) = Offset.Zero
        }

        assertFalse(emptyFakeDataSet.iterator().hasNext())
        assertTrue(singleFakeDataSet.iterator().hasNext())
        assertEquals(Offset.Zero, singleFakeDataSet.iterator().nextOffset())
        assertEquals(Offset.Zero, singleFakeDataSet.iterator().next())
    }

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

    @Test
    fun testEmpty() {
        assertNull(emptySet.firstOrNull())
        assertNull(emptySet.lastOrNull())
    }
}