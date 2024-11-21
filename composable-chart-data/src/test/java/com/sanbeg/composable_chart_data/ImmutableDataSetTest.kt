package com.sanbeg.composable_chart_data

import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalStdlibApi::class)
class ImmutableDataSetTest {

    private val list = listOf(
        Point(12f, 34f),
        Point(56f, 78f),
    )

    @Test
    fun `it can round trip from a list by iteration`() {
        val sut = ImmutableDataSet(2) { i: Int ->
            list[i]
        }

        val mutableList = mutableListOf<Point>()

        sut.forEach(mutableList::add)

        assertEquals(list, mutableList)
    }

    @Test
    fun `it can round trip from a list by iteration2`() {
        val sut = ImmutableDataSet(2) { i: Int ->
            list[i]
        }

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `it can round trip from a list`() {
        val sut = ImmutableDataSet(list)

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `it can create from two arrays`() {
        val sut = ImmutableDataSet(floatArrayOf(12f, 56f), floatArrayOf(34f, 78f))

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `it can create from one array`() {
        val sut = ImmutableDataSet(floatArrayOf(12f, 34f, 56f, 78f))

        assertEquals(list, sut.map { it })
    }

    @Test
    fun testSize() {
        val sut = ImmutableDataSet(list)

        assertEquals(2, sut.size)
    }

    @Test
    fun testContains() {
        val sut = ImmutableDataSet(list)

        assertTrue(sut.contains(Point(12f, 34f)))
    }

    @Test
    fun testNotContains() {
        val sut = ImmutableDataSet(list)

        assertFalse(sut.contains(Point(1234f, 1234f)))
    }


    @Test
    fun testPlus() {
        val extra = Point(1234f, 1234f)
        val sut = ImmutableDataSet(list).plus(extra)

        assertEquals(list.plus(extra), sut.map { it })
    }

    @Test
    fun testPlusCollection() {
        val extra = Point(1234f, 1234f)
        val collection = ImmutableDataSet(listOf(extra))
        val sut = ImmutableDataSet(list).plus(collection)

        assertEquals(list.plus(extra), sut.map { it })
    }

}