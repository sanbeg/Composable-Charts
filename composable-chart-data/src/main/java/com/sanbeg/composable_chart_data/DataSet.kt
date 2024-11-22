package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable
import com.sanbeg.composable_chart_data.function.IndexedPointConsumer
import com.sanbeg.composable_chart_data.function.PointBinaryOperator
import com.sanbeg.composable_chart_data.function.PointConsumer
import com.sanbeg.composable_chart_data.function.PointPredicate
import com.sanbeg.composable_chart_data.function.PointToFloatFunction

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

fun DataSet.forEach(action: PointConsumer) {
    for (i in indices) {
        action(get(i))
    }
}

fun DataSet.onEach(action: PointConsumer): DataSet {
    forEach(action)
    return this
}

fun DataSet.forEachIndexed(action: IndexedPointConsumer) {
    for (i in indices) {
        action(i, get(i))
    }
}

fun DataSet.first() = this[0]
fun DataSet.last() = this[lastIndex]

fun DataSet.firstOrNull() = if (size > 0) this[0] else null
fun DataSet.lastOrNull() = if (size > 0) this[size - 1] else null

fun DataSet.firstOrNull(predicate: PointPredicate): Point? {
    for (it in indices) {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

fun DataSet.lastOrNull(predicate: PointPredicate): Point? {
    for (it in indices.reversed()) {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

// TODO - this still boxes
fun <R>DataSet.fold(initial: R, operation: (acc: R, Point) -> R): R {
    var acc = initial
    for (i in indices) {
        acc = operation(acc, get(i))
    }
    return acc
}

private fun DataSet.fastReduce(operation: PointBinaryOperator): Point {
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

fun DataSet.maxBy(selector: PointToFloatFunction): Point = fastReduce{acc, cur ->
    if (selector(cur) > selector(acc)) cur else acc
}

fun DataSet.minBy(selector: PointToFloatFunction): Point = fastReduce{acc, cur ->
    if (selector(cur) < selector(acc)) cur else acc
}

fun DataSet.maxX2() = maxBy(Point::x)
fun DataSet.maxX3() = maxBy{it.x}