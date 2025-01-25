
package com.sanbeg.composable_chart.core

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sanbeg.composable_chart_data.geometry.ChartRange
import com.sanbeg.composable_chart_data.geometry.FloatPair

internal val ModifierLocalRangeX = modifierLocalOf { ChartRange.Normal }
internal val ModifierLocalRangeY = modifierLocalOf { ChartRange.Normal }
internal val ModifierLocalDataInset = modifierLocalOf { 0.dp }
internal val ModifierLocalLogBase = modifierLocalOf { FloatPair.Unspecified }

/**
 * Modifier which specifies the X range of the chart.
 * @param[range] The value which will scale [ChartRange.start] to the left and [ChartRange.end]
 * to the right side of the chart.
 *
 * Note than start and end should not be the same.  If start < end, then increasing values
 * will be drawn closer to the left.  Reversing start and end would cause the graph to be flipped.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.xRange(range: ChartRange) = modifierLocalProvider(ModifierLocalRangeX) { range }

/**
 * Modifier which specifies the X range of the chart.
 *
 * @param[start] The value which will scale to the left side of the chart
 * @param[end] The value which will scale to the right side of the chart.
 *
 * Note than start and end should not be the same.  If start < end, then increasing values
 * will be drawn closer to the left.  Reversing start and end would cause the graph to be flipped.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.xRange(start: Float, end: Float) =
    modifierLocalProvider(ModifierLocalRangeX) { ChartRange(start, end) }

/**
 * Modifier which specifies the Y range of the chart.
 * @param[range] The value which will scale [ChartRange.start] to the bottom and [ChartRange.end]
 * to the top of the chart.
 *
 * Note than start and end should not be the same.  If start < end, then increasing values
 * will be drawn closer to the top.  Reversing start and end would cause the graph to be inverted.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.yRange(range: ChartRange) = modifierLocalProvider(ModifierLocalRangeY) { range }

/**
 * Modifier which specifies the Y range of the chart.
 *
 * @param[start] The value which will scale to the top of the chart
 * @param[end] The value which will scale to the bottom of the chart.
 *
 * Note than start and end should not be the same.  If start < end, then increasing values
 * will be drawn closer to the top.  Reversing start and end would cause the graph to be inverted.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.yRange(start: Float, end: Float) =
    modifierLocalProvider(ModifierLocalRangeY) { ChartRange(start, end) }

/**
 * Modifier which specifies an inset for plot data.
 *
 * By matching the chart range to the data range, and the plot inset to the circle radius, a scatter
 * diagram could be drawn with the edges of the circles aligned with the axis lines.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.plotInset(inset: Dp) = modifierLocalProvider(ModifierLocalDataInset) { inset }

/**
 * Sets the chart to be drawn in log scale on one or both axis.
 *
 * Currently both axis can be used for data, while only the Y axis can be used for functions.
 * Drawing the axis tics in log scale isn't supported yet.
 */
@ExperimentalChartApi
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.logScale(x: Float = 0f, y: Float = 0f) = modifierLocalProvider(ModifierLocalLogBase) { FloatPair(x, y)}