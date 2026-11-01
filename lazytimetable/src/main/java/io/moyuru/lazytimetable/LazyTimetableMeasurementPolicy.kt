@file:OptIn(ExperimentalFoundationApi::class)

package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection

/**
 * Creates a measurement policy for LazyTimetable that handles the layout and positioning
 * of timetable items, column headers, time labels, and backgrounds.
 *
 * The policy implements viewport culling to only measure and place visible items,
 * and handles z-ordering to ensure proper layering of components.
 *
 * @param state The state of the LazyTimetable
 * @param scope The scope containing layout information like columns, headers, and time labels
 * @return A measurement policy function
 */
@OptIn(ExperimentalFoundationApi::class)
internal fun lazyTimetableMeasurementPolicy(
  contentPadding: PaddingValues,
  state: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
): LazyLayoutMeasureScope.(Constraints) -> MeasureResult = { constraints ->
  val visibleItems = mutableListOf<VisibleItem>()
  val previousVisibleColumn = (state.firstVisibleColumnNumber - 1).coerceAtLeast(0)
  val scrollYOffset = state.scrollYOffset
  val scrollXOffset = state.scrollXOffset

  for (columnNumber in previousVisibleColumn until scope.columnCount) {
    val column = scope.columns[columnNumber]
    val firstPeriod = column.firstOrNull() ?: continue
    val x = firstPeriod.x + scrollXOffset
    if (x + firstPeriod.width < 0 || x > constraints.maxWidth) continue
    for (positionInColumn in firstPeriodIndexReachingViewportTop(column, scrollYOffset) until column.size) {
      val period = column[positionInColumn]
      val y = period.y + scrollYOffset
      if (y > constraints.maxHeight) break
      visibleItems.add(
        VisibleItem(
          x,
          y,
          0f,
          period.columnNumber,
          measure(
            period.positionInItemProvider,
            Constraints(
              minWidth = period.width,
              maxWidth = period.width,
              minHeight = period.height,
              maxHeight = period.height,
            ),
          ).first(),
        ),
      )
    }
  }

  state.firstVisibleColumnNumber = visibleItems.firstOrNull()?.columnNumber ?: -1
  state.lastVisibleColumnNumber = visibleItems.lastOrNull()?.columnNumber ?: -1
  state.scrollHorizontalMin = minOf(
    0,
    constraints.maxWidth - scope.contentWidth -
      contentPadding.calculateRightPadding(LayoutDirection.Ltr).roundToPx(),
  )
  state.scrollVerticalMin = minOf(
    0,
    constraints.maxHeight - scope.contentHeight - contentPadding.calculateBottomPadding().roundToPx(),
  )

  layout(constraints.maxWidth, constraints.maxHeight) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y, it.z)
    }
  }
}

internal fun firstPeriodIndexReachingViewportTop(column: List<Period>, scrollYOffset: Int): Int {
  var low = 0
  var high = column.size
  while (low < high) {
    val mid = (low + high) ushr 1
    val period = column[mid]
    if (period.y + period.height + scrollYOffset < 0) low = mid + 1 else high = mid
  }
  return low
}
