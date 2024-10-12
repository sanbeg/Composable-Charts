package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import kotlin.math.min

private fun Offset.toLong() = packFloats(x, y)
private fun toOffset(l: Long) = Offset(unpackFloat1(l), unpackFloat2(l))

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
}
