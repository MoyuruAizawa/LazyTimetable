@file:OptIn(ExperimentalFoundationApi::class)

package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max


/**
 * Creates a measurement policy for LazyTimetable that handles the layout and positioning
 * of timetable items, column headers, time labels, and backgrounds.
 * 
 * The policy implements viewport culling to only measure and place visible items,
 * and handles z-ordering to ensure proper layering of components.
 * 
 * @param listState The state of the LazyTimetable
 * @param scope The scope containing layout information like columns, headers, and time labels
 * @return A measurement policy function
 */
@OptIn(ExperimentalFoundationApi::class)
internal fun lazyTimetableMeasurementPolicy(
  listState: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
): LazyLayoutMeasureScope.(Constraints) -> MeasureResult = { constraints ->
  val visibleItems = mutableListOf<VisibleItem>()
  val previousVisibleColumn = (listState.firstVisibleColumnNumber - 1).coerceAtLeast(0)
  val scrollYOffset = listState.scrollYOffset
  val scrollXOffset = listState.scrollXOffset

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
          isPeriod = true,
        )
      )
    }
  }

  val periods = visibleItems.filter { it.isPeriod }
  val visibleFirstPeriod = periods.firstOrNull()
  val visibleLastPeriod = periods.lastOrNull()
  listState.firstVisibleColumnNumber = visibleFirstPeriod?.columnNumber ?: -1
  listState.lastVisibleColumnNumber = visibleLastPeriod?.columnNumber ?: -1
  listState.scrollHorizontalMin =
    scope.columns.lastOrNull()?.firstOrNull()?.let {
      constraints.maxWidth -
          (it.x + it.width) -
          scope.contentPadding.calculateRightPadding(LayoutDirection.Ltr).roundToPx()
    } ?: 0
  var mostBottom = 0
  scope.columns.forEach { value ->
    mostBottom = max(mostBottom, value.lastOrNull()?.let { it.y + it.height } ?: 0)
  }
  listState.scrollVerticalMin = constraints.maxHeight -
      mostBottom -
      scope.contentPadding.calculateBottomPadding().roundToPx()

  layout(constraints.maxWidth, constraints.maxHeight) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y, it.z)
    }
  }
}

/**
 * Represents a visible item in the timetable layout.
 * 
 * @param x The x-coordinate position for placement
 * @param y The y-coordinate position for placement
 * @param z The z-order (depth) for layering
 * @param columnNumber The column number this item belongs to, or -1 for non-column items
 * @param placeable The measured placeable for this item
 * @param isPeriod Whether this item represents a period (true) or other content like headers (false)
 */
internal class VisibleItem(
  val x: Int,
  val y: Int,
  val z: Float,
  val columnNumber: Int,
  val placeable: Placeable,
  val isPeriod: Boolean = false,
)
