package com.sanbeg.composable_chart_data.geometry

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@JvmInline
value class InclusiveFloatRange internal constructor(private val packedValue: Long) {
    constructor(a: Float, b: Float) : this(packFloats(a, b))

    @Stable
    val start: Float get() = unpackFloat1(packedValue)

    @Stable
    val end: Float get() = unpackFloat2(packedValue)

    @Stable
    operator fun component1() = unpackFloat1(packedValue)

    @Stable
    operator fun component2() = unpackFloat2(packedValue)
}