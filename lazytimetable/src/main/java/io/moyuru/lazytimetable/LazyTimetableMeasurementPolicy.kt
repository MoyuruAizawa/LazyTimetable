@file:OptIn(ExperimentalFoundationApi::class)

package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import kotlin.math.max

@Composable
internal fun rememberMeasurementPolicy(
  listState: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
) = remember { measurementPolicy(listState, scope) }

@OptIn(ExperimentalFoundationApi::class)
private fun measurementPolicy(
  listState: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
): LazyLayoutMeasureScope.(Constraints) -> MeasureResult = { constraints ->
  val visibleItems = mutableListOf<VisibleItem>()
  val previousVisibleColumn = (listState.firstVisibleColumnNumber - 1).coerceAtLeast(0)
  val scrollYOffset = listState.scrollYOffset + scope.columnHeaderHeightPx
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
    val columnHeader = scope.columnHeaders[columnNumber]
    val x = columnHeader.x + scrollXOffset
    when {
      x + columnHeader.width < 0 -> continue
      x > constraints.maxWidth -> break
    }
    visibleItems.add(
      VisibleItem(
        x = x,
        y = columnHeader.y,
        columnNumber = columnNumber,
        placeable = measure(
          columnHeader.positionInItemProvider,
          Constraints(
            minWidth = columnHeader.width,
            maxWidth = columnHeader.width,
            minHeight = columnHeader.height,
            maxHeight = columnHeader.height,
          )
        ).first()
      )
    )
  }

  val visibleFirstItem = visibleItems.firstOrNull()
  val visibleLastItem = visibleItems.lastOrNull()
  listState.firstVisibleColumnNumber = visibleFirstItem?.columnNumber ?: -1
  listState.lastVisibleColumnNumber = visibleLastItem?.columnNumber ?: -1
  listState.scrollHorizontalMin =
    scope.columns.lastOrNull()?.firstOrNull()?.let { constraints.maxWidth - (it.x + it.width) } ?: 0
  var mostBottom = 0
  scope.columns.forEach { value ->
    mostBottom = max(mostBottom, value.lastOrNull()?.let { it.y + it.height + scope.columnHeaderHeightPx } ?: 0)
  }
  listState.scrollVerticalMin = constraints.maxHeight - mostBottom

  layout(constraints.maxWidth, constraints.maxHeight) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y)
    }
  }
}

private class VisibleItem(
  val x: Int,
  val y: Int,
  val columnNumber: Int,
  val placeable: Placeable,
)
