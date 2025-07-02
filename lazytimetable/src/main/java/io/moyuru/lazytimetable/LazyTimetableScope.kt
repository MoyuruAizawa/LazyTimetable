package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable

interface LazyTimetableScope {
  fun column(columnContent: LazyTimetableColumnScope.() -> Unit)
}

interface LazyTimetableColumnScope {
  fun item(
    durationSec: Int,
    content: @Composable LazyTimetableItemScope.() -> Unit
  )
}

interface LazyTimetableItemScope

internal class LazyTimetableScopeImpl(
  private val columnWidthPx: Int,
  private val heightPerMinutePx: Int,
) : LazyTimetableScope {
  private val _items = mutableListOf<Period>()
  internal val items: List<Period> = _items
  private val _columns = ArrayList<List<Period>>()
  internal val columns: List<List<Period>> = _columns
  internal val columnCount get() = columns.size

  override fun column(
    columnContent: LazyTimetableColumnScope.() -> Unit
  ) {
    val column = mutableListOf<Period>()
    columnContent(
      LazyTimetableColumnScopeImpl(
        columnWidthPx = columnWidthPx,
        heightPerMinutePx = heightPerMinutePx,
        columnNumber = columns.size,
        items = _items,
        column = column,
      )
    )
    _columns.add(column)
  }
}

internal class LazyTimetableColumnScopeImpl(
  private val columnWidthPx: Int,
  private val heightPerMinutePx: Int,
  private val columnNumber: Int,
  private val items: MutableList<Period>,
  private val column: MutableList<Period>,
) : LazyTimetableColumnScope {

  override fun item(
    durationSec: Int,
    content: @Composable (LazyTimetableItemScope.() -> Unit)
  ) {
    val previousBottom = column.getOrNull(column.lastIndex)?.let { it.y + it.height }
    val period = Period(
      columnNumber = columnNumber,
      positionInColumn = column.size,
      positionInItemProvider = items.size,
      width = columnWidthPx,
      height = (durationSec / 60) * heightPerMinutePx,
      x = columnWidthPx * columnNumber,
      y = previousBottom ?: 0,
      content = content,
    )

    items.add(period)
    column.add(period)
  }
}

internal class LazyTimetableItemScopeImpl : LazyTimetableItemScope
