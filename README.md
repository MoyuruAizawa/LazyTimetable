# LazyTimetable

A high-performance, lazy-loading timetable composable for Android Jetpack Compose.  
Perfect for creating festival schedules, conference schedules, and any time-based multi-column layouts.

## ✨ Features

- **🚀 High Performance**: Lazy loading with viewport culling for smooth scrolling even with large datasets
- **⏰ Time-based Positioning**: Items are positioned based on duration in seconds with automatic layout calculation
- **📱 Bi-directional Scrolling**: Smooth horizontal and vertical scrolling
- **🎨 Highly Customizable**: Configure colors, spacing, and content appearance
- **🔧 Type-safe DSL**: Clean, declarative API for defining timetable structure

## 🛠️ Installation

TBD

## 🚀 Quick Start

Here's a simple example of creating a timetable:

```kotlin
LazyTimetable(
  contentPadding = contentPaddings,
  horizontalSpacing = 4.dp,
  columnWidth = 120.dp,
  heightPerMinute = 1.5.dp,
  columnHeaderHeight = 80.dp,
  columnHeaderColor = Color.White,
  timeColumnWidth = 100.dp,
  timeColumnColor = Color.White,
  baseEpochSec = tomorrowland.startAt,
  timeLabel = { TimeLabel(it) },
  modifier = Modifier.fillMaxSize(),
) {
  festival.stages.forEach { stage ->
    column(header = { Header(stage) }) {
      stage.periods.forEach { period ->
        item(period.durationSec) {
          when (period) {
            is Period.Empty -> Spacer(Modifier)
            is Period.Show -> Show(period.title)
          }
        }
      }
    }
  }
}
```

## 📚 API Reference

### LazyTimetable

The main composable function for creating a timetable.

```kotlin
@Composable
fun LazyTimetable(
    modifier: Modifier = Modifier,
    listState: LazyTimetableState = rememberLazyTimetableState(),
    horizontalSpacing: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(),
    columnWidth: Dp,
    heightPerMinute: Dp,
    columnHeaderHeight: Dp,
    columnHeaderColor: Color,
    timeColumnWidth: Dp,
    timeColumnColor: Color,
    baseEpochSec: Long,
    timeLabel: @Composable (Long) -> Unit,
    content: LazyTimetableScope.() -> Unit
)
```

#### Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `listState` | `LazyTimetableState` | State object that provides scroll state information |
| `horizontalSpacing` | `Dp` | Horizontal spacing between columns |
| `contentPadding` | `PaddingValues` | Padding around the entire timetable |
| `columnWidth` | `Dp` | Width of each column in the timetable |
| `heightPerMinute` | `Dp` | Height allocated per minute |
| `columnHeaderHeight` | `Dp` | Height of the column headers |
| `columnHeaderColor` | `Color` | Background color of the column headers |
| `timeColumnWidth` | `Dp` | Width of the time column (left side time labels) |
| `timeColumnColor` | `Color` | Background color of the time column (left side time labels) |
| `baseEpochSec` | `Long` | Base timestamp in epoch seconds used as the reference point for time calculations |
| `timeLabel` | `@Composable (Long) -> Unit` | Composable function for rendering time labels, receives epoch seconds as parameter |
| `content` | `LazyTimetableScope.() -> Unit` | DSL block for defining the timetable structure using LazyTimetableScope |

### LazyTimetableScope

The receiver scope for defining timetable content.

```kotlin
interface LazyTimetableScope {
    fun column(
        header: @Composable () -> Unit,
        columnContent: LazyTimetableColumnScope.() -> Unit
    )
    
    fun timeLabel(timeLabel: @Composable (Long) -> Unit)
}
```

#### Functions

- **`column`**: Adds a column to the timetable with a header and content
- **`timeLabel`**: Defines how time labels are displayed (automatically generated for each hour)

### LazyTimetableColumnScope

The receiver scope for defining column content.

```kotlin
interface LazyTimetableColumnScope {
    fun item(
        durationSec: Int,
        content: @Composable () -> Unit
    )
}
```

#### Functions

- **`item`**: Adds an item to the column with specified duration in seconds
