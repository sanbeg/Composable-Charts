package com.sanbeg.composable_chart.collections

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.math.min

private fun Offset.toLong() = packFloats(x, y)

@Immutable
class ImmutableOffsetCollection private constructor(private val array: LongArray): OffsetCollection {
    constructor(size: Int, init: (Int) -> Offset) : this(
        LongArray(size) { i ->
            init(i).let {
                packFloats(it.x, it.y)
            }
        }
    )

    // constructor(size: Int, init: (Int) -> Long) : this(LongArray(size) { init(it) })

    constructor(floatArray: FloatArray) : this(
        LongArray(floatArray.size/2) { i ->
            packFloats(floatArray[i*2], floatArray[i*2+1])
        })

    constructor(x: FloatArray, y: FloatArray) : this(
        LongArray(min(x.size, y.size)) { i ->
            packFloats(x[i], y[i])
        }
    )

    constructor(collection: Collection<Offset>) : this(
        LongArray(collection.size).also {
            collection.forEachIndexed { index, offset ->
                it[index] = offset.toLong()
            }
        }
    )

    override val size = array.size

    override fun contains(element: Offset) = array.contains(element.toLong())

    override fun containsAll(elements: Collection<Offset>) = elements.fold(true) { acc, offset ->
        acc && contains(offset)
    }

    override fun isEmpty() = array.isEmpty()

    override fun iterator(): Iterator<Offset> = OffsetIterator(array.iterator())

    private class OffsetIterator(private val li: LongIterator) : Iterator<Offset> {
        private fun toOffset(l: Long) = Offset(unpackFloat1(l), unpackFloat2(l))
        override fun hasNext() = li.hasNext()
        override fun next() = toOffset(li.next())
    }

    fun mapOffset(transform: (Offset) -> Offset): ImmutableOffsetCollection {
        val rv = LongArray(size)
        forEachIndexed { i, offset ->
            rv[i] = transform(offset).toLong()
        }
        return ImmutableOffsetCollection(rv)
    }

    operator fun plus(offset: Offset) = ImmutableOffsetCollection(array + offset.toLong())
    operator fun plus(other: ImmutableOffsetCollection) = ImmutableOffsetCollection(array + other.array)

    fun windowed(
        size: Int,
        step: Int,
        partialWindows: Boolean = false,
        transform: (ImmutableOffsetCollection) -> Unit
    ) = (array.indices step step).forEach { start ->
        val slice = try {
            array.sliceArray(start.rangeUntil(start + size))
        } catch (e: IndexOutOfBoundsException) {
            if (partialWindows)
                array.sliceArray(start.rangeUntil(array.size))
            else
                longArrayOf()
        }
        if (slice.isNotEmpty()) {
            transform(ImmutableOffsetCollection(slice))
        }
    }
}

fun Iterable<Offset>.mapOffset(transform: (Offset) -> Offset): Collection<Offset> =
    if (this is ImmutableOffsetCollection) {
        mapOffset(transform)
    } else {
        map(transform)
    }

fun Iterable<Offset>.windowedOffset(
    size: Int,
    step: Int,
    partialWindows: Boolean = false,
    transform: (Collection<Offset>) -> Unit
) {
    if (this is ImmutableOffsetCollection) {
        windowed(size, step, partialWindows, transform)
    } else {
        windowed(size, step, partialWindows, transform)
    }
}