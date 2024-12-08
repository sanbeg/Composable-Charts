package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Immutable
import com.sanbeg.composable_chart_data.function.IntToPointFunction
import com.sanbeg.composable_chart_data.geometry.Point
import com.sanbeg.composable_chart_data.geometry.packFloats
import kotlin.math.min

@ExperimentalStdlibApi
@JvmInline
@Immutable
value class ImmutableDataSet private constructor(private val array: LongArray): DataSet {
    constructor(size: Int, init: IntToPointFunction) : this(
        LongArray(size) { i ->
            init(i).let(Point::packedValue)
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

    constructor(collection: Collection<Point>) : this(
        LongArray(collection.size).also {
            collection.forEachIndexed { index, offset ->
                it[index] = offset.packedValue
            }
        }
    )

    override val size
        get() = array.size

    override fun get(index: Int) = Point(array[index])

    operator fun plus(point: Point) = ImmutableDataSet(array + point.packedValue)
    operator fun plus(other: ImmutableDataSet) = ImmutableDataSet(array + other.array)

    fun contains(point: Point) = array.contains(point.packedValue)

    fun mapOffsets(transform: (Point) -> Point): ImmutableDataSet {
        val rv = LongArray(size)
        forEachIndexed { i, point ->
            rv[i] = transform(point).packedValue
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
