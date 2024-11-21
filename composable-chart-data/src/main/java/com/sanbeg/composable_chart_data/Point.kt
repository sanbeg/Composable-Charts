package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.floatFromBits
import androidx.compose.ui.util.packFloats


// Masks everything but the sign bit
internal const val DualUnsignedFloatMask = 0x7fffffff_7fffffffL

// Any value greater than this is a NaN
internal const val FloatInfinityBase = 0x7f800000L
internal const val DualFloatInfinityBase = 0x7f800000_7f800000L

// Same as Offset/Size.Unspecified.packedValue, but avoids a getstatic
internal const val UnspecifiedPackedFloats = 0x7fc00000_7fc00000L // NaN_NaN

// Set the highest bit of each 32 bit chunk in a 64 bit word
internal const val Uint64High32 = -0x7fffffff_80000000L
// Set the lowest bit of each 32 bit chunk in a 64 bit word
internal const val Uint64Low32 = 0x00000001_00000001L

/**
 * Packs two Float values into one Long value for use in inline classes.
 */
inline fun packFloats(val1: Float, val2: Float): Long {
    val v1 = val1.toRawBits().toLong()
    val v2 = val2.toRawBits().toLong()
    return (v1 shl 32) or (v2 and 0xFFFFFFFF)
}

/**
 * Unpacks the first Float value in [packFloats] from its returned Long.
 */
inline fun unpackFloat1(value: Long): Float {
    return floatFromBits((value shr 32).toInt())
}


/**
 * Unpacks the second Float value in [packFloats] from its returned Long.
 */
inline fun unpackFloat2(value: Long): Float {
    return floatFromBits((value and 0xFFFFFFFF).toInt())
}


@JvmInline
@Stable
value class Point(val packedValue: Long) {
    constructor(x: Float, y: Float) : this(packFloats(x, y))

    @Stable
    val x: Float get() = unpackFloat1(packedValue)

    @Stable
    val y: Float get() = unpackFloat2(packedValue)

    @Stable
    inline operator fun component1(): Float = x

    @Stable
    inline operator fun component2(): Float = y

    @Stable
    companion object {
        val Unspecified = Point(UnspecifiedPackedFloats)
    }
}


/**
 * True if both x and y values of the [Offset] are finite. NaN values are not
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
 * `false` when this is [Offset.Unspecified].
 */
@Stable
val Point.isSpecified: Boolean
    get() = packedValue and DualUnsignedFloatMask != UnspecifiedPackedFloats

/**
 * `true` when this is [Offset.Unspecified].
 */
@Stable
val Point.isUnspecified: Boolean
    get() = packedValue and DualUnsignedFloatMask == UnspecifiedPackedFloats
