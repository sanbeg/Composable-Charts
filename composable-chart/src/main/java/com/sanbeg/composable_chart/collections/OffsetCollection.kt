package com.sanbeg.composable_chart.collections

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

@Stable
interface OffsetCollection: Collection<Offset> {
    // fun map(transform: (Offset) -> Offset): OffsetCollection
}