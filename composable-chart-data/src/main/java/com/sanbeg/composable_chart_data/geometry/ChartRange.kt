package com.sanbeg.composable_chart_data.geometry

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.math.max
import kotlin.math.min

@Immutable
@JvmInline
/**
 * An immutable floating point range.  The range is always inclusive.
 */
value class ChartRange internal constructor(private val packedValue: Long) {
    constructor(a: Float, b: Float) : this(packFloats(a, b))

    @Stable
    val start: Float get() = unpackFloat1(packedValue)

    @Stable
    val end: Float get() = unpackFloat2(packedValue)

    @Stable
    operator fun component1() = unpackFloat1(packedValue)

    @Stable
    operator fun component2() = unpackFloat2(packedValue)

    @Stable
    fun isEmpty() = start == end
}

val ChartRange.min get() = min(start, end)
val ChartRange.max get() = max(start, end)
fun ChartRange.reversed() = ChartRange(end, start)
fun ChartRange.sorted() = ChartRange(min, max)
fun ChartRange.toFloatRange(): ClosedFloatingPointRange<Float> = min.rangeTo(max)
fun ClosedFloatingPointRange<Float>.toChartRange() = ChartRange(start, endInclusive)