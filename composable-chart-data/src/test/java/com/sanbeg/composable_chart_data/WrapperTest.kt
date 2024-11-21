package com.sanbeg.composable_chart_data

import org.junit.Assert.*
import org.junit.Test

class WrapperTest {
    private val list = (0 .. 10).map { Point(it.toFloat(), it.toFloat()) }
    private val array = Array(10) {
        Point(it.toFloat(), it.toFloat())
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
        val it = array.asDataSet().asIterable().iterator()
        array.forEach { offset ->
            assertEquals(offset, it.nextPoint())
        }
        assertFalse(it.hasNext())
    }

    @Test
    fun testListIterator() {
        val it = list.asDataSet().asIterable().iterator()
        list.forEach { offset ->
            assertEquals(offset, it.next())
        }
        assertFalse(it.hasNext())
    }

}