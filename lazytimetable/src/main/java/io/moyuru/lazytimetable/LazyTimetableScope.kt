package io.moyuru.lazytimetable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

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
  density: Density,
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  verticalSpacing: Dp,
  horizontalSpacing: Dp,
  internal val contentPadding: PaddingValues,
) : LazyTimetableScope, Density by density {
  private val _items = mutableListOf<@Composable () -> Unit>()
  internal val items: List<@Composable () -> Unit> = _items
  private val _columnHeaders = ArrayList<ColumnHeader>()
  internal val columnHeaders: List<ColumnHeader> = _columnHeaders
  private val _columns = ArrayList<List<Period>>()
  internal val columns: List<List<Period>> = _columns
  internal val columnCount get() = columns.size

  private val columnWidthPx = columnWidth.roundToPx()
  private val heightPerMinutePx = heightPerMinute.roundToPx()
  private val columnHeaderHeightPx = columnHeaderHeight.roundToPx()
  private val verticalSpacingPx = verticalSpacing.roundToPx()
  private val horizontalSpacingPx = horizontalSpacing.roundToPx()

  internal var columnHeaderBottom = columnHeaderHeight.roundToPx()

  override fun column(
    header: @Composable () -> Unit,
    columnContent: LazyTimetableColumnScope.() -> Unit,
  ) {
    val headerTop = contentPadding.calculateTopPadding().roundToPx()
    val columnNumber = columns.size
    val columnHeader = ColumnHeader(
      columnNumber = columnNumber,
      positionInItemProvider = items.size,
      width = columnWidthPx,
      height = columnHeaderHeightPx,
      x = (columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber +
          contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx()),
      y = headerTop,
    )
    columnHeaderBottom = headerTop + columnHeaderHeightPx
    _items.add(header)
    _columnHeaders.add(columnHeader)

    val column = mutableListOf<Period>()
    columnContent(
      LazyTimetableColumnScopeImpl(
        density = this,
        columnHeaderBottom = headerTop + columnHeaderHeightPx,
        columnWidthPx = columnWidthPx,
        heightPerMinutePx = heightPerMinutePx,
        verticalSpacingPx = verticalSpacingPx,
        horizontalSpacingPx = horizontalSpacingPx,
        contentPadding = contentPadding,
        columnNumber = columnNumber,
        items = _items,
        column = column,
      )
    )
    _columns.add(column)
  }
}

internal class LazyTimetableColumnScopeImpl(
  density: Density,
  private val columnHeaderBottom: Int,
  private val columnWidthPx: Int,
  private val heightPerMinutePx: Int,
  private val verticalSpacingPx: Int,
  private val horizontalSpacingPx: Int,
  private val contentPadding: PaddingValues,
  private val columnNumber: Int,
  private val items: MutableList<@Composable () -> Unit>,
  private val column: MutableList<Period>,
) : LazyTimetableColumnScope, Density by density {

  override fun item(
    durationSec: Int,
    content: @Composable () -> Unit
  ) {
    val previousBottom = column.getOrNull(column.lastIndex)?.let { it.y + it.height } ?: columnHeaderBottom
    val period = Period(
      columnNumber = columnNumber,
      positionInColumn = column.size,
      positionInItemProvider = items.size,
      width = columnWidthPx,
      height = (durationSec / 60) * heightPerMinutePx,
      x = columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber +
          contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx(),
      y = previousBottom + verticalSpacingPx,
    )

    items.add(content)
    column.add(period)
  }
}
