package com.sanbeg.composable_chart_data.geometry

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlin.math.max
import kotlin.math.min

@Immutable
@JvmInline
/**
 * An immutable floating point range, used to represent a range of values in a chart.
 *
 * The range is always inclusive.
 *
 *  [start] and [end] should not be the same.  If [start] < [end], then increasing values
 *  will be drawn closer to the top or left.  Reversing [start] and [end] would cause the graph to be reversed.
 */
value class ChartRange internal constructor(private val packedValue: Long) {
    constructor(a: Float, b: Float) : this(packFloats(a, b))

    /** The beginning value of the range */
    @Stable
    val start: Float get() = unpackFloat1(packedValue)

    /** The end value of the range */
    @Stable
    val end: Float get() = unpackFloat2(packedValue)

    @Stable
    operator fun component1() = unpackFloat1(packedValue)

    @Stable
    operator fun component2() = unpackFloat2(packedValue)

    /** Returns true for an empty range */
    @Stable
    fun isEmpty() = start == end

    @Stable
    override fun toString() = "ChartRange($start ${if (start>end) ">=" else "<="} $end)"

    @Stable
    companion object {
        val Normal = ChartRange(0f, 1f)
    }
}

/** Returns the minimum value of the range */
fun ChartRange.min() = min(start, end)
/** Returns the maximum value of the range */
fun ChartRange.max() = max(start, end)
/** Returns the difference of end - start */
fun ChartRange.length() = end - start

/** Reversed the range from its current order */
fun ChartRange.reversed() = ChartRange(end, start)
/** Converts the range to increasing order */
fun ChartRange.sorted() = ChartRange(min(), max())

/** Ensures there is some spread in the range, but expanding by 0.5 in each direction if start == end */
fun ChartRange.ensureSpread() = if (start == end) ChartRange(start-0.5f, end+0.5f) else this

/** Expand the range to include 0 */
fun ChartRange.includeZero() = when {
    end > start && start > 0 -> ChartRange(0f, end) // increasing, positive
    end > start && end < 0 -> ChartRange(start, 0f) // increasing, negative
    end < start && end > 0 -> ChartRange(start, 0f) // decreasing, positive
    end < start && start < 0 -> ChartRange(0f, end) // decreasing, negative
    else -> this
}

/** Convert the chart range into a Kotlin range.  This does not preserve the order, since Kotlin ranges are always increasing */
fun ChartRange.toFloatRange(): ClosedFloatingPointRange<Float> = min().rangeTo(max())
fun ClosedFloatingPointRange<Float>.toChartRange() = ChartRange(start, endInclusive)

// should add functions to combine ranges, round to even numbers