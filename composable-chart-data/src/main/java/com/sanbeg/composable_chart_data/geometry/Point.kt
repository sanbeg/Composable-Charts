package com.sanbeg.composable_chart_data.geometry

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

/**
 * An immutable 2D floating point data point.
 *
 * Points are generally interpreted as data coordinates in some unspecified units.
 */
@JvmInline
@Immutable
value class Point(val packedValue: Long) {
    constructor(x: Float, y: Float) : this(packFloats(x, y))

    @Stable
    val x: Float get() = unpackFloat1(packedValue)

    @Stable
    val y: Float get() = unpackFloat2(packedValue)

    @Stable
    operator fun component1() = unpackFloat1(packedValue)

    @Stable
    operator fun component2() = unpackFloat2(packedValue)

    @Stable
    override fun toString() = "Point(x=$x, y=$y)"

    @Stable
    fun contentToString() = "[$x,$y]"

    /**
     * Returns a copy of this Point instance optionally overriding the
     * x or y parameter
     */
    fun copy(x: Float = unpackFloat1(packedValue), y: Float = unpackFloat2(packedValue)) =
        Point(packFloats(x, y))

    @Stable
    companion object {
        /**
         * Represents an unspecified [Point] value, usually a replacement for `null`
         * when a primitive value is desired.
         */
        val Unspecified = Point(UnspecifiedPackedFloats)
    }
}


/**
 * True if both x and y values of the [Point] are finite. NaN values are not
 * considered finite.
 */
@Stable
val Point.isFinite: Boolean get() {
    // Mask out the sign bit and do an equality check in each 32-bit lane
    // against the "infinity base" mask (to check whether each packed float
    // is infinite or not).
    val v = (packedValue and DualFloatInfinityBase) xor DualFloatInfinityBase
    return (v - Uint64Low32) and v.inv() and Uint64High32 == 0L
}

/**
 * `false` when this is [Point.Unspecified].
 */
@Stable
val Point.isSpecified: Boolean
    get() = packedValue and DualUnsignedFloatMask != UnspecifiedPackedFloats

/**
 * `true` when this is [Point.Unspecified].
 */
@Stable
val Point.isUnspecified: Boolean
    get() = packedValue and DualUnsignedFloatMask == UnspecifiedPackedFloats
