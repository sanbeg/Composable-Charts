package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import com.sanbeg.composable_chart_data.geometry.InclusiveFloatRange
import com.sanbeg.composable_chart_data.geometry.Point
import kotlin.math.max
import kotlin.math.min

/**
 * An ordered collection of [Point]s.
 * Methods in this interface support read-only access to the collection.
 *
 * @see dataSetOf
 * @see asDataSet
 */
@Stable
interface DataSet {
    /** Returns the size of the collection. */
    val size: Int

    /** Returns the Point at the given index.  This method can be called using the index operator. */
    operator fun get(index: Int): Point
}

@Stable
val DataSet.indices: IntRange
    get() = 0 until size

@Stable
val DataSet.lastIndex: Int
    get() = size - 1

/**
 * Performs the given [action] on each element.
 */
inline fun DataSet.forEach(action: (Point) -> Unit) {
    for (i in indices) {
        action(get(i))
    }
}

private fun DataSet.testsum(): Float {
    var sum = 0f
    forEach{sum += it.x}
    return sum
}

/**
 * Performs the given [action] on each element and returns the DataSet itself afterwards.
 */
inline fun DataSet.onEach(action: (Point) -> Unit): DataSet {
    forEach(action)
    return this
}

/**
 * Performs the given [action] on each element, providing sequential index with the element.
 * @param [action] function that takes the index of an element and the element itself
 * and performs the action on the element.
 */
inline fun DataSet.forEachIndexed(action: (Int, Point) -> Unit) {
    for (i in indices) {
        action(i, get(i))
    }
}

/**
 * Returns the first element.
 *
 * @throws NoSuchElementException if the DataSet is empty.
 */
fun DataSet.first() = this[0]
/**
 * Returns the last element.
 *
 * @throws NoSuchElementException if the DataSet is empty.
 */
fun DataSet.last() = this[lastIndex]

/**
 * Returns the first element, or `null` if the DataSet is empty.
 */
fun DataSet.firstOrNull() = if (size > 0) this[0] else null
/**
 * Returns the last element, or `null` if the DataSet is empty.
 */
fun DataSet.lastOrNull() = if (size > 0) this[size - 1] else null

inline fun DataSet.firstOrNull(predicate: (Point) -> Boolean): Point? {
    for (it in indices) {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

inline fun DataSet.lastOrNull(predicate: (Point) -> Boolean): Point? {
    for (it in indices.reversed()) {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

/**
 * Accumulates value starting with [initial] value and applying [operation] from left to right
 * to current accumulator value and each element.
 *
 * Returns the specified [initial] value if the array is empty.
 *
 * @param [operation] function that takes current accumulator value and an element, and calculates the next accumulator value.
 */
inline fun <R>DataSet.fold(initial: R, operation: (acc: R, Point) -> R): R {
    var acc = initial
    for (i in indices) {
        acc = operation(acc, get(i))
    }
    return acc
}

inline fun DataSet.reduce(operation: (Point, Point) -> Point): Point {
    var acc = first()
    for (i in 1 until size) {
        acc = operation(acc, get(i))
    }
    return acc
}

inline fun DataSet.maxByOrNull(selector: (Point) -> Float): Point? {
    if (size == 0) return null
    var acc = first()
    for (i in 1 until size) {
        val cur = get(i)
        if (selector(cur) > selector(acc)) acc = cur
    }
    return acc
}

inline fun DataSet.maxBy(selector: (Point) -> Float): Point = reduce { acc, cur ->
    if (selector(cur) > selector(acc)) cur else acc
}

inline fun DataSet.minBy(selector: (Point) -> Float): Point = reduce { acc, cur ->
    if (selector(cur) < selector(acc)) cur else acc
}

fun DataSet.xrange(): InclusiveFloatRange {
    val init = get(0).x
    return fold(InclusiveFloatRange(init, init)) { range, point ->
        val cur = point.x
        InclusiveFloatRange(min(range.start, cur), max(range.end, cur))
    }
}

fun DataSet.yrange(): InclusiveFloatRange {
    val init = get(0).y
    return fold(InclusiveFloatRange(init, init)) { range, point ->
        val cur = point.y
        InclusiveFloatRange(min(range.start, cur), max(range.end, cur))
    }
}