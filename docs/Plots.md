# Plot types

The library supports several plot types.

## line

`line(dataSet, color=Color.Black)`

![line](../composable-chart/src/debug/screenshotTest/reference/com/sanbeg/composable_chart/PreviewPlotTypes/PlotPreview_748aa731_4f59fc0b_0.png)

## scatter

`scatter(dataSet, radius=1.dp, color=Color.Black)`

![scatter](../composable-chart/src/debug/screenshotTest/reference/com/sanbeg/composable_chart/PreviewPlotTypes/PlotPreview_748aa731_4f59fc0b_1.png)

## area

`area(dataSet, brush=SolidColor(Color.Cyan))`

![scatter](../composable-chart/src/debug/screenshotTest/reference/com/sanbeg/composable_chart/PreviewPlotTypes/PlotPreview_748aa731_4f59fc0b_2.png)


## step

Step graphs can be drawn with the step either after or before the
point.  This can be seem more easily when drawn in conjunction with a
scatter plot, i.e.

```kotlin
step(it, color=Color.Blue)
scatter(it, radius=1.dp, color=Color.Black)
```

![step](../composable-chart/src/debug/screenshotTest/reference/com/sanbeg/composable_chart/PreviewPlotTypes/PlotPreview_748aa731_4f59fc0b_4.png)

```kotlin
step(it, color=Color.Blue, where=StepVertical.Pre)
scatter(it, radius=1.dp, color=Color.Black)
```

![scatter](../composable-chart/src/debug/screenshotTest/reference/com/sanbeg/composable_chart/PreviewPlotTypes/PlotPreview_748aa731_4f59fc0b_5.png)