package io.moyuru.lazytimetable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/**
 * Receiver scope for [LazyTimetable] content.
 */
interface LazyTimetableScope {
  /**
   * Adds a column to the timetable.
   *
   * @param header The header composable for this column.
   * @param columnContent The content of this column, defined using [LazyTimetableColumnScope].
   */
  fun column(
    header: @Composable () -> Unit,
    columnContent: LazyTimetableColumnScope.() -> Unit,
  )

  /**
   * Adds time labels to the timetable.
   *
   * @param timeLabel The composable function to display time labels. The parameter is the time in epoch seconds.
   */
  fun timeLabel(
    timeLabel: @Composable (epochSec: Long) -> Unit,
  )
}

/**
 * Receiver scope for column content in [LazyTimetableScope.column].
 */
interface LazyTimetableColumnScope {
  /**
   * Adds a single item to the column.
   *
   * @param durationSec The duration of this item in seconds.
   * @param content The composable content for this item.
   */
  fun item(
    durationSec: Int,
    content: @Composable () -> Unit
  )
}

/**
 * Implementation of [LazyTimetableColumnScope] that accumulates column items.
 *
 * @param accumulator Function to accumulate items with their duration and content.
 */
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

/**
 * Internal implementation of [LazyTimetableScope] that handles the virtual measurement and positioning
 * of timetable items.
 *
 * @param density Density for converting between Dp and pixels.
 * @param columnWidth Width of each column in Dp.
 * @param heightPerMinute Height per minute for time-based positioning in Dp.
 * @param timeColumnWidth Width of the time column in Dp.
 * @param horizontalSpacing Horizontal spacing between columns in Dp.
 * @param baseEpochSec Base time in epoch seconds for calculating relative positions.
 * @param contentPadding Padding around the timetable content.
 */
internal class LazyTimetableScopeImpl(
  density: Density,
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  timeColumnWidth: Dp,
  horizontalSpacing: Dp,
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
  internal val columnCount get() = columns.size

  private val columnWidthPx = columnWidth.roundToPx()
  private val heightPerMinutePx = heightPerMinute.roundToPx()
  private val columnHeaderHeightPx = columnHeaderHeight.roundToPx()
  private val timeColumnWidthPx = timeColumnWidth.roundToPx()

  private val horizontalSpacingPx = horizontalSpacing.roundToPx()

  /**
   * Estimates the position and size of a period.
   */
  private fun estimatePeriod(
    position: Int,
    columnNumber: Int,
    previous: Period?,
    durationSec: Int,
    content: @Composable () -> Unit,
  ): Period {
    val previousBottom = previous?.bottom ?: 0
    val startAt = previous?.endAtSec ?: baseEpochSec
    return Period(
      columnNumber = columnNumber,
      positionInItemProvider = position,
      startAtSec = startAt,
      endAtSec = startAt + durationSec,
      width = columnWidthPx,
      height = (durationSec / 60) * heightPerMinutePx,
      x = columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber,
      y = previousBottom,
      content = content,
    )
  }

  override fun column(
    header: @Composable () -> Unit,
    columnContent: LazyTimetableColumnScope.() -> Unit,
  ) {
    val columnNumber = columns.size
    val columnHeader = ColumnHeader(
      width = columnWidthPx,
      height = columnHeaderHeightPx,
      x = columnWidthPx * columnNumber +
          horizontalSpacingPx * columnNumber,
      y = 0,
      content = header,
    )
    _columnHeaders.add(columnHeader)

    val column = mutableListOf<Period>()
    columnContent(
      LazyTimetableColumnScopeImpl { durationSec, content ->
        val previous = column.getOrNull(column.lastIndex)
        val period = estimatePeriod(
          items.size,
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
        timeColumnWidthPx,
        60 * heightPerMinutePx,
        paddingLeft,
        (it - baseEpochSec).toInt() / 60 * heightPerMinutePx,
        { timeLabel(it) },
      )
      _timeLabels.add(measuredTimeLabel)
    }
  }
}

