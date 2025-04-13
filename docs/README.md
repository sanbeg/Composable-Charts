# Composable Charts

This library provides a toolkit to generate data charts with Jetpack Compose.

# Usage

To create a chart, we'll start with an example data set.

```kotlin
private val chartData = listOf(
        Point(0f, 0f),
        Point(10f, 10f),
        Point(15f, 7f),
        Point(25f, 30f),
        Point(40f, 40f),
        Point(50f, 50f)
    ).asDataSet()
```

Here, the `Point` class represents the x and y coordinates of each
data point, in whatever logical units we want to represent in our
chart.  The `List.asDataSet()` extension allows us to present our list
as a `DataSet`, which is a simple interface used by the charts.  While
in this case since we're using a small set, we just use the `List`
wrapper; if we were using larger sets, we'd likely use one of the more
performant implementations which avoids boxing our data.

Once we have this data, we can render it in a chart, like this:

```kotlin
       Chart(
            Modifier
                .size(150.dp)
                .xRange(chartData.xRange())
                .yRange(chartData.yRange())
        ) {
            Plot {
                line(chartData)
            }
        }

    }
```

This would produce something like

!(../composable-chart/src/debug/screenshotTest/reference/com/sanbeg/composable_chart/ExamplePreviewsScreenshots/GreetingPreview_748aa731_0.png)
