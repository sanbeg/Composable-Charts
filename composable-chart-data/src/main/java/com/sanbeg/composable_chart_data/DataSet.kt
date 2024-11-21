package com.sanbeg.composable_chart_data

import androidx.compose.runtime.Stable

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

fun DataSet.forEach(action: (Point) -> Unit) = indices.forEach {
    action(get(it))
}

fun DataSet.onEach(action: (Point) -> Unit): DataSet {
    forEach(action)
    return this
}

fun DataSet.forEachIndexed(action: (index: Int, Point) -> Unit) =
    indices.forEachIndexed { i, o ->
        action(i, get(o))
    }

@Deprecated("use asList().map()")
fun DataSet.map(transform: (Point) -> Point): List<Point> =
    mapStableDataSet(this, transform)

private fun mapStableDataSet(data: DataSet, transform: (Point) -> Point): List<Point> =
    buildList {
        data.forEach { add(transform(it)) }
    }

fun DataSet.first() = this[0]
fun DataSet.last() = this[lastIndex]

fun DataSet.firstOrNull() = if (size > 0) this[0] else null
fun DataSet.lastOrNull() = if (size > 0) this[size - 1] else null

fun DataSet.firstOrNull(predicate: (Point) -> Boolean): Point? {
    indices.forEach {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

fun DataSet.lastOrNull(predicate: (Point) -> Boolean): Point? {
    indices.reversed().forEach {
        val o = this[it]
        if (predicate(o)) return o
    }
    return null
}

fun <R>DataSet.fold(initial: R, operation: (acc: R, Point) -> R): R {
    var acc = initial
    for (i in indices) {
        acc = operation(acc, get(i))
    }
    return acc
}