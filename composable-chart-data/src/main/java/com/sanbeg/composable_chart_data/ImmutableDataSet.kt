package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
//import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.math.min


/**
 * Packs two Float values into one Long value for use in inline classes.
 */
private fun packFloats(val1: Float, val2: Float): Long {
    val v1 = val1.toRawBits().toLong()
    val v2 = val2.toRawBits().toLong()
    return (v1 shl 32) or (v2 and 0xFFFFFFFF)
}


private fun Offset.toLong() = if (this.isSpecified) packFloats(x, y) else packFloats(Float.NaN, Float.NaN)
private fun toOffset(l: Long) = Offset(unpackFloat1(l), unpackFloat2(l))

/*
 * note - with composeBom = "2024.04.01", an unspecified Offset would throw an exception when
 * trying to access either element, or with many other operations which at that time depended on
 * the X & Y values. That logic seems to be gone from composeBom = "2024.10.00" (ui.geometry=1.7.4).
 *
 * As of 10/24, master may expose the constructor & packed long, if that comes to pass we can skip
 * some of this pack/unpack translation/
 */

@JvmInline
@Immutable
value class ImmutableDataSet private constructor(private val array: LongArray): StableDataSet {
    @JvmInline
    private value class DataSetLongIterator(private val iterator: LongIterator) : OffsetIterator {
        override fun hasNext() = iterator.hasNext()
        override fun next() = toOffset(iterator.next())
        override fun nextOffset() = toOffset(iterator.nextLong())
    }

    constructor(size: Int, init: (Int) -> Offset) : this(
        LongArray(size) { i ->
            init(i).let {
                packFloats(it.x, it.y)
            }
        }
    )

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

    override val size
        get() = array.size

    override fun get(index: Int) = toOffset(array[index])

    override fun iterator(): OffsetIterator = DataSetLongIterator(array.iterator())

    operator fun plus(offset: Offset) = ImmutableDataSet(array + offset.toLong())
    operator fun plus(other: ImmutableDataSet) = ImmutableDataSet(array + other.array)

    fun contains(offset: Offset) = array.contains(offset.toLong())

    fun mapOffsets(transform: (Offset) -> Offset): ImmutableDataSet {
        val rv = LongArray(size)
        forEachIndexed { i, offset ->
            rv[i] = transform(offset).toLong()
        }
        return ImmutableDataSet(rv)
    }

    fun forEachOffsetWindow(
        size: Int,
        step: Int,
        partialWindows: Boolean = false,
        action: (ImmutableDataSet) -> Unit
    ): Unit = (array.indices step step).forEach { start ->
        val slice = try {
            array.sliceArray(start.rangeUntil(start + size))
        } catch (e: IndexOutOfBoundsException) {
            if (partialWindows)
                array.sliceArray(start.rangeUntil(array.size))
            else
                LongArray(0)
        }
        if (slice.isNotEmpty()) {
            action(ImmutableDataSet(slice))
        }
    }
}
