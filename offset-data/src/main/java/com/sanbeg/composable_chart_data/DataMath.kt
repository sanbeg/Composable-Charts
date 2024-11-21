package com.sanbeg.composable_chart_data

import kotlin.math.max
import kotlin.math.min

interface DataMath {
    fun minX(): Float
    fun maxX(): Float
    fun avgX(): Float
    fun minY(): Float
    fun maxY(): Float
    fun avgY(): Float
}

fun DataSet.minX(): Float = if (this is DataMath) {
    this.minX()
} else if (size == 0) {
    Float.NaN
} else {
    fold(Float.POSITIVE_INFINITY) { a, o ->
        min(a, o.x)
    }
}

fun DataSet.minY(): Float = if (this is DataMath) {
    this.minX()
} else if (size == 0) {
    Float.NaN
} else {
    fold(Float.POSITIVE_INFINITY) { a, o ->
        min(a, o.y)
    }
}

fun DataSet.maxX(): Float = if (this is DataMath) {
    this.minX()
} else if (size == 0) {
    Float.NaN
} else {
    fold(Float.NEGATIVE_INFINITY) { a, o ->
        max(a, o.x)
    }
}

fun DataSet.maxY(): Float = if (this is DataMath) {
    this.minX()
} else if (size == 0) {
    Float.NaN
} else {
    fold(Float.NEGATIVE_INFINITY) { a, o ->
        max(a, o.y)
    }
}

fun DataSet.avgX(): Float = if (this is DataMath) {
    this.avgX()
} else if (size == 0) {
    Float.NaN
} else {
    val sum = fold(0f) {a, o ->
        a + o.x
    }
    sum / size
}