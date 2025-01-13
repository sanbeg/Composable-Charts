package com.sanbeg.composable_chart_data.geometry

import androidx.compose.runtime.Stable

@JvmInline
value class FloatPair internal constructor(internal val packedValue: Long) {
    constructor(a: Float, b: Float) : this(packFloats(a, b))

    @Stable
    val first: Float get() = unpackFloat1(packedValue)

    @Stable
    val second: Float get() = unpackFloat2(packedValue)

    @Stable
    operator fun component1() = unpackFloat1(packedValue)

    @Stable
    operator fun component2() = unpackFloat2(packedValue)

    @Stable
    companion object {
        /**
         * Represents an unspecified [Point] value, usually a replacement for `null`
         * when a primitive value is desired.
         */
        val Unspecified = FloatPair(UnspecifiedPackedFloats)
    }
}


/**
 * `false` when this is [Point.Unspecified].
 */
@Stable
val FloatPair.isSpecified: Boolean
    get() = packedValue and DualUnsignedFloatMask != UnspecifiedPackedFloats

/**
 * `true` when this is [Point.Unspecified].
 */
@Stable
val FloatPair.isUnspecified: Boolean
    get() = packedValue and DualUnsignedFloatMask == UnspecifiedPackedFloats
