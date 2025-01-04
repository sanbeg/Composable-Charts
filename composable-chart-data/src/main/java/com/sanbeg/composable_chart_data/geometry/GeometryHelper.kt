package com.sanbeg.composable_chart_data.geometry


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
internal fun packFloats(val1: Float, val2: Float): Long {
    val v1 = val1.toRawBits().toLong()
    val v2 = val2.toRawBits().toLong()
    return (v1 shl 32) or (v2 and 0xFFFFFFFF)
}


private fun floatFromBits(bits: Int): Float = java.lang.Float.intBitsToFloat(bits)

/**
 * Unpacks the first Float value in [packFloats] from its returned Long.
 */
internal fun unpackFloat1(value: Long): Float {
    return floatFromBits((value shr 32).toInt())
}

/**
 * Unpacks the second Float value in [packFloats] from its returned Long.
 */
internal fun unpackFloat2(value: Long): Float {
    return floatFromBits((value and 0xFFFFFFFF).toInt())
}
