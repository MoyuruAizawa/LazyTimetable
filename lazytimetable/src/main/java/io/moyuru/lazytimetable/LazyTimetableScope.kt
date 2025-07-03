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

  fun timeLabel(
    timeLabel: @Composable (Long) -> Unit,
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
  timeColumnWidth: Dp,
  verticalSpacing: Dp,
  horizontalSpacing: Dp,
  private val baseEpochSec: Long,
  internal val contentPadding: PaddingValues,
) : LazyTimetableScope, Density by density {
  private val _items = mutableListOf<@Composable () -> Unit>()
  internal val items: List<@Composable () -> Unit> = _items
  private val _columnHeaders = ArrayList<ColumnHeader>()
  internal val columnHeaders: List<ColumnHeader> = _columnHeaders
  private val _timeLabels = ArrayList<TimeLabel>()
  internal val timeLabels: List<TimeLabel> = _timeLabels
  private val _columns = ArrayList<List<Period>>()
  internal val columns: List<List<Period>> = _columns
  internal val columnCount get() = columns.size

  private val columnWidthPx = columnWidth.roundToPx()
  private val heightPerMinutePx = heightPerMinute.roundToPx()
  private val columnHeaderHeightPx = columnHeaderHeight.roundToPx()
  private val timeColumnWidthPx = timeColumnWidth.roundToPx()
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
      x = timeColumnWidthPx +
          columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber +
          contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx(),
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
        timeColumnWidth = timeColumnWidthPx,
        contentPadding = contentPadding,
        columnNumber = columnNumber,
        baseEpochSec = baseEpochSec,
        items = _items,
        column = column,
      )
    )
    _columns.add(column)
  }

  override fun timeLabel(timeLabel: @Composable ((Long) -> Unit)) {
    val allPeriods = columns.flatMap { it }
    val start = allPeriods.minBy { it.startAtSec }
    val end = allPeriods.maxBy { it.endAtSec }

//    columns.flatMap { it }
//      .distinctBy { it.startAtSec }
//      .forEach {
//        _items.add { timeLabel(it.startAtSec) }
//        _timeLabels.add(
//          TimeLabel(
//            _items.lastIndex,
//            timeColumnWidthPx,
//            contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx(),
//            it.y,
//          )
//        )
//      }
  }
}

internal class LazyTimetableColumnScopeImpl(
  density: Density,
  private val columnHeaderBottom: Int,
  private val columnWidthPx: Int,
  private val heightPerMinutePx: Int,
  private val verticalSpacingPx: Int,
  private val horizontalSpacingPx: Int,
  private val timeColumnWidth: Int,
  private val contentPadding: PaddingValues,
  private val columnNumber: Int,
  private val baseEpochSec: Long,
  private val items: MutableList<@Composable () -> Unit>,
  private val column: MutableList<Period>,
) : LazyTimetableColumnScope, Density by density {

  override fun item(
    durationSec: Int,
    content: @Composable () -> Unit
  ) {
    val previous = column.getOrNull(column.lastIndex)
    val previousBottom = previous?.let { it.y + it.height } ?: columnHeaderBottom
    val startAt = previous?.endAtSec ?: baseEpochSec
    val period = Period(
      columnNumber = columnNumber,
      positionInColumn = column.size,
      positionInItemProvider = items.size,
      startAtSec = startAt,
      endAtSec = startAt + durationSec,
      width = columnWidthPx,
      height = (durationSec / 60) * heightPerMinutePx,
      x = timeColumnWidth +
          columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber +
          contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx(),
      y = previousBottom + verticalSpacingPx,
    )

    items.add(content)
    column.add(period)
  }
}
