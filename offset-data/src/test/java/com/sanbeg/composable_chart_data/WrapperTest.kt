package com.sanbeg.composable_chart_data

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.*
import org.junit.Test

class WrapperTest {
    private val list = (0 .. 10).map { Offset(it.toFloat(), it.toFloat()) }
    private val array = Array(10) {
        Offset(it.toFloat(), it.toFloat())
    }

    @Test
    fun testListSize() {
        assertEquals(list.size, list.asDataSet().size)
    }

    @Test
    fun testArraySize() {
        assertEquals(array.size, array.asDataSet().size)
    }

    @Test
    fun testListGet() {
        assertEquals(list[3], list.asDataSet()[3])
    }

    @Test
    fun testArrayGet() {
        assertEquals(array[3], array.asDataSet()[3])
    }

    @Test
    fun testArrayIterator() {
        val it = array.asDataSet().iterator()
        array.forEach { offset ->
            assertEquals(offset, it.nextOffset())
        }
        assertFalse(it.hasNext())
    }

    @Test
    fun testListIterator() {
        val it = list.asDataSet().iterator()
        list.forEach { offset ->
            assertEquals(offset, it.next())
        }
        assertFalse(it.hasNext())
    }

}