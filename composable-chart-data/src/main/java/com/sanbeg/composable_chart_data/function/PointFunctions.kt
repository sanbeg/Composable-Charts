package com.sanbeg.composable_chart_data.function

import com.sanbeg.composable_chart_data.Point


/*
* functional interfaces inspired by java.util.function, to avoid the boxing which would
* happen if we passed value classes to lambdas
*/

/**
 * Represents an operation that accepts a [Point] argument and returns no result.
 *
 * This is a [functional interface](https://kotlinlang.org/docs/fun-interfaces.html)
 */
fun interface PointConsumer {
    operator fun invoke (point: Point)
}

/**
 * Represents an operation that accepts [Int] and [Point] arguments and returns no result.
 *
 * This is a [functional interface](https://kotlinlang.org/docs/fun-interfaces.html)
 */
fun interface IndexedPointConsumer {
    operator fun invoke (index: Int, point: Point)
}

fun interface IndexPointProducer {
    operator fun invoke (index: Int) : Point
}

// PointSuppler: () -> Boolean

/**
 * Represents a predicate (boolean-valued function) of one [Point] argument.
 *
 * This is a [functional interface](https://kotlinlang.org/docs/fun-interfaces.html)
 */
fun interface PointPredicate {
    operator fun invoke (point: Point) : Boolean
}

/**
 * Represents an operation upon two [Point] operands and producing a [Point] result.
 *
 * This is a [functional interface](https://kotlinlang.org/docs/fun-interfaces.html)
 */
fun interface PointBinaryOperator {
    operator fun invoke (a: Point, b: Point) : Point
}

/**
 * Represents a function that accepts a [Point] argument and produces an boolean-valued result.
 *
 * This is a [functional interface](https://kotlinlang.org/docs/fun-interfaces.html)
 */
fun interface PointToFloatFunction {
    operator fun invoke (point: Point) : Float
}