@file:OptIn(ExperimentalComposeUiApi::class)

package com.sanbeg.composable_chart.charts

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.modifier.modifierLocalProvider
import com.sanbeg.composable_chart_data.geometry.ChartRange

internal val ModifierLocalRangeX = modifierLocalOf { ChartRange.Normal }
internal val ModifierLocalRangeY = modifierLocalOf { ChartRange.Normal }


fun Modifier.xRange(range: ChartRange) = modifierLocalProvider(ModifierLocalRangeX){range}
fun Modifier.xRange(start: Float, end: Float) = modifierLocalProvider(ModifierLocalRangeX){ChartRange(start, end)}

fun Modifier.yRange(range: ChartRange) = modifierLocalProvider(ModifierLocalRangeY){range}
fun Modifier.yRange(start: Float, end: Float) = modifierLocalProvider(ModifierLocalRangeY){ChartRange(start, end)}