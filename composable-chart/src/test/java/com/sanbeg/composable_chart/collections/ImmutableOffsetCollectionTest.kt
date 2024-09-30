package com.sanbeg.composable_chart.collections

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.*
import org.junit.Test

class ImmutableOffsetCollectionTest {
    private fun ImmutableOffsetCollection.iocToList() = map { it }

    private val list = listOf(
        Offset(12f, 34f),
        Offset(56f, 78f),
    )

    @Test
    fun `it can round trip from a list by iteration`() {
        val sut = ImmutableOffsetCollection(2) {i: Int ->
            list[i]
        }

        val mutableList = mutableListOf<Offset>()

        sut.forEach(mutableList::add)

        assertEquals(list, mutableList)
    }

    @Test
    fun `it can round trip by map`() {
        val sut = ImmutableOffsetCollection(2) {i ->
            list[i]
        }

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `it can round trip from a list`() {
        val sut = ImmutableOffsetCollection(list)

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `it can create from two arrays`() {
        val sut = ImmutableOffsetCollection(floatArrayOf(12f, 56f), floatArrayOf(34f, 78f))

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `it can create from one array`() {
        val sut = ImmutableOffsetCollection(floatArrayOf(12f, 34f, 56f, 78f))

        assertEquals(list, sut.map { it })
    }

    @Test
    fun `extension can map collection`() {
        val sut = ImmutableOffsetCollection(list)

        val mapped = sut.mapOffset { it.times(2f) }.map { it }

        assertEquals(list.map { it.times(2f) }, mapped)
    }

    @Test
    fun `extension can map iterable`() {

        assertEquals(list.map { it.times(2f) }, list.mapOffset { it.times(2f) })
    }

    @Test
    fun testSize() {
        val sut = ImmutableOffsetCollection(list)

        assertEquals(2, sut.size)
    }

    @Test
    fun testContains() {
        val sut = ImmutableOffsetCollection(list)

        assertTrue(sut.contains(Offset(12f, 34f)))
    }

    @Test
    fun testNotContains() {
        val sut = ImmutableOffsetCollection(list)

        assertFalse(sut.contains(Offset(1234f, 1234f)))
    }

    @Test
    fun testContainsAll() {
        val sut = ImmutableOffsetCollection(list)

        assertTrue(sut.containsAll(list))
    }

    @Test
    fun testNotContainsAll() {
        val sut = ImmutableOffsetCollection(list)

        assertFalse(sut.containsAll(list.plus(Offset(1234f, 1234f))))
    }

    @Test
    fun testPlus() {
        val extra = Offset(1234f, 1234f)
        val sut = ImmutableOffsetCollection(list).plus(extra)

        assertEquals(list.plus(extra), sut.map { it })
    }
    @Test
    fun testPlusCollection() {
        val extra = Offset(1234f, 1234f)
        val collection = ImmutableOffsetCollection(listOf(extra))
        val sut = ImmutableOffsetCollection(list).plus(collection)

        assertEquals(list.plus(extra), sut.map { it })
    }


    @Test
    fun testWindow() {
        val sut = ImmutableOffsetCollection(3) {
            Offset(it.toFloat(), it.toFloat())
        }

        val expected = listOf(
            ImmutableOffsetCollection(listOf(Offset(0f, 0f), Offset(1f, 1f))),
            ImmutableOffsetCollection(listOf(Offset(1f, 1f), Offset(2f, 2f)))
        )

        val actual = mutableListOf<ImmutableOffsetCollection>()
        sut.windowed(2, 1) {
            actual.add(it)
        }

        assertEquals(
            expected.map{ it.iocToList()},
            actual.map{ it.iocToList()}
        )
    }

}