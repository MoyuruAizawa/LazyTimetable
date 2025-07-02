package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable

interface LazyTimetableScope {
  fun column(
    header: @Composable () -> Unit,
    columnContent: LazyTimetableColumnScope.() -> Unit,
  )
}

interface LazyTimetableColumnScope {
  fun item(
    durationSec: Int,
    content: @Composable () -> Unit
  )
}

internal class LazyTimetableScopeImpl(
  private val columnWidthPx: Int,
  private val heightPerMinutePx: Int,
  internal val columnHeaderHeightPx: Int,
) : LazyTimetableScope {
  private val _items = mutableListOf<@Composable () -> Unit>()
  internal val items: List<@Composable () -> Unit> = _items
  private val _columnHeaders = ArrayList<ColumnHeader>()
  internal val columnHeaders: List<ColumnHeader> = _columnHeaders
  private val _columns = ArrayList<List<Period>>()
  internal val columns: List<List<Period>> = _columns
  internal val columnCount get() = columns.size

  override fun column(
    header: @Composable () -> Unit,
    columnContent: LazyTimetableColumnScope.() -> Unit,
  ) {
    val columnNumber = columns.size
    val columnHeader = ColumnHeader(
      columnNumber = columnNumber,
      positionInItemProvider = items.size,
      width = columnWidthPx,
      height = columnHeaderHeightPx,
      x = columnWidthPx * columnNumber,
      y = 0,
    )
    _items.add(header)
    _columnHeaders.add(columnHeader)

    val column = mutableListOf<Period>()
    columnContent(
      LazyTimetableColumnScopeImpl(
        columnWidthPx = columnWidthPx,
        heightPerMinutePx = heightPerMinutePx,
        columnNumber = columnNumber,
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
  private val items: MutableList<@Composable () -> Unit>,
  private val column: MutableList<Period>,
) : LazyTimetableColumnScope {

  override fun item(
    durationSec: Int,
    content: @Composable () -> Unit
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
    )

    items.add(content)
    column.add(period)
  }
}
