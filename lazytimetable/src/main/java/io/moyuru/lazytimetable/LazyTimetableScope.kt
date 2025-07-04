package io.moyuru.lazytimetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

class LazyTimetableColumnScopeImpl(
  private val accumulator: (durationSec: Int, content: @Composable () -> Unit) -> Unit,
) : LazyTimetableColumnScope {
  override fun item(
    durationSec: Int,
    content: @Composable () -> Unit
  ) {
    accumulator(durationSec, content)
  }
}

internal class LazyTimetableScopeImpl(
  density: Density,
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  timeColumnWidth: Dp,
  verticalSpacing: Dp,
  horizontalSpacing: Dp,
  private val columnHeaderColor: Color,
  private val timeColumnColor: Color,
  private val baseEpochSec: Long,
  internal val contentPadding: PaddingValues,
) : LazyTimetableScope, Density by density {
  private val _items = mutableListOf<VirtualMeasuredItem>()
  internal val items: List<VirtualMeasuredItem> = _items
  private val _columnHeaders = ArrayList<ColumnHeader>()
  internal val columnHeaders: List<ColumnHeader> = _columnHeaders
  private val _timeLabels = ArrayList<TimeLabel>()
  internal val timeLabels: List<TimeLabel> = _timeLabels
  private val _columns = ArrayList<List<Period>>()
  internal val columns: List<List<Period>> = _columns
  internal var columnHeaderBackground: ColumnHeaderBackground? = null
    private set
  internal var timeColumnBackground: TimeColumnBackground? = null
    private set
  internal val columnCount get() = columns.size

  private val columnWidthPx = columnWidth.roundToPx()
  private val heightPerMinutePx = heightPerMinute.roundToPx()
  private val columnHeaderHeightPx = columnHeaderHeight.roundToPx()
  private val timeColumnWidthPx = timeColumnWidth.roundToPx()

  private val verticalSpacingPx = verticalSpacing.roundToPx()
  private val horizontalSpacingPx = horizontalSpacing.roundToPx()

  internal val timetableViewPortTop = (contentPadding.calculateTopPadding() + columnHeaderHeight).roundToPx()
  internal val timetableViewPortLeft = contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx() +
      timeColumnWidthPx

  fun estimateColumnHeader(
    columnNumber: Int,
    timetableViewPortLeft: Int,
    paddingTop: Int,
    content: @Composable () -> Unit,
  ): ColumnHeader {
    return ColumnHeader(
      positionInItemProvider = items.size,
      width = columnWidthPx,
      height = columnHeaderHeightPx,
      x = timetableViewPortLeft +
          columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber,
      y = paddingTop,
      content = content,
    )
  }

  fun estimatePeriod(
    columnNumber: Int,
    previous: Period?,
    durationSec: Int,
    content: @Composable () -> Unit,
  ): Period {
    val previousBottom = previous?.bottom ?: timetableViewPortTop
    val startAt = previous?.endAtSec ?: baseEpochSec
    return Period(
      columnNumber = columnNumber,
      positionInItemProvider = items.size,
      startAtSec = startAt,
      endAtSec = startAt + durationSec,
      width = columnWidthPx,
      height = (durationSec / 60) * heightPerMinutePx,
      x = timetableViewPortLeft +
          columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber,
      y = previousBottom + verticalSpacingPx,
      content = content,
    )
  }

  override fun column(
    header: @Composable () -> Unit,
    columnContent: LazyTimetableColumnScope.() -> Unit,
  ) {
    val columnNumber = columns.size
    val columnHeader = estimateColumnHeader(
      columnNumber,
      timetableViewPortLeft,
      contentPadding.calculateTopPadding().roundToPx(),
      header,
    )
    _items.add(columnHeader)
    _columnHeaders.add(columnHeader)

    val column = mutableListOf<Period>()
    columnContent(
      LazyTimetableColumnScopeImpl { durationSec, content ->
        val previous = column.getOrNull(column.lastIndex)
        val period = estimatePeriod(
          columnNumber,
          previous,
          durationSec,
          content,
        )
        _items.add(period)
        column.add(period)
      }
    )
    _columns.add(column)
  }

  override fun timeLabel(timeLabel: @Composable ((Long) -> Unit)) {
    val allPeriods = columns.flatMap { it }
    val start = allPeriods.minBy { it.startAtSec }.startAtSec
    val end = allPeriods.maxBy { it.endAtSec }.endAtSec
    val paddingLeft = contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx()
    generateSequence(start) { previous ->
      val next = previous + 60 * 60
      if (next < end) next else null
    }.forEach {
      val measuredTimeLabel = TimeLabel(
        _items.size,
        timeColumnWidthPx,
        60 * heightPerMinutePx,
        paddingLeft,
        timetableViewPortTop + (it - baseEpochSec).toInt() / 60 * heightPerMinutePx,
        { timeLabel(it) },
      )
      _items.add(measuredTimeLabel)
      _timeLabels.add(measuredTimeLabel)
    }
  }

  internal fun background() {
    val columnHeaderBackground = ColumnHeaderBackground(
      x = 0,
      y = 0,
      height = timetableViewPortTop,
      positionInItemProvider = _items.size,
      content = {
        Spacer(
          Modifier
            .fillMaxSize()
            .background(columnHeaderColor)
        )
      }
    )
    _items.add(columnHeaderBackground)
    this.columnHeaderBackground = columnHeaderBackground

    val timeColumnBackground = TimeColumnBackground(
      x = 0,
      y = timetableViewPortTop,
      width = timetableViewPortLeft,
      positionInItemProvider = _items.size,
      content = {
        Spacer(
          Modifier
            .fillMaxSize()
            .background(timeColumnColor)
        )
      }
    )
    _items.add(timeColumnBackground)
    this.timeColumnBackground = timeColumnBackground
  }
}

