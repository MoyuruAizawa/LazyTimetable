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
    for (positionInColumn in 0 until column.size) {
      val period = column[positionInColumn]
      val x = period.x + scrollXOffset
      val y = period.y + scrollYOffset
      when {
        x + period.width < 0 -> break
        x > constraints.maxWidth -> break
        y + period.height < 0 -> continue
        y > constraints.maxHeight -> break
      }
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
            )
          ).first(),
        )
      )
    }
  }

  state.firstVisibleColumnNumber = visibleItems.firstOrNull()?.columnNumber ?: -1
  state.lastVisibleColumnNumber = visibleItems.lastOrNull()?.columnNumber ?: -1
  state.scrollHorizontalMin =
    scope.columns.lastOrNull()?.firstOrNull()?.let {
      constraints.maxWidth -
          (it.x + it.width) -
          contentPadding.calculateRightPadding(LayoutDirection.Ltr).roundToPx()
    } ?: 0
  state.scrollVerticalMin = constraints.maxHeight -
      scope.columns.maxOf { it.lastOrNull()?.bottom ?: 0 } -
      contentPadding.calculateBottomPadding().roundToPx()

  layout(constraints.maxWidth, constraints.maxHeight) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y, it.z)
    }
  }
}
