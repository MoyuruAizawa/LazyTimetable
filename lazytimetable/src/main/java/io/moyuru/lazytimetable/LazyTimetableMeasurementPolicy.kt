@file:OptIn(ExperimentalFoundationApi::class)

package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

private const val COLUMN_HEADER_BACKGROUND_Z = 3f
private const val COLUMN_HEADER_Z = COLUMN_HEADER_BACKGROUND_Z + 1
private const val TIME_COLUMN_BACKGROUND_Z = 2f
private const val TIME_COLUMN_Z = TIME_COLUMN_BACKGROUND_Z + 1
private const val TIMETABLE_Z = 1f


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
  val scrollYOffset = listState.scrollYOffset
  val scrollXOffset = listState.scrollXOffset

  for (columnNumber in previousVisibleColumn until scope.columnCount) {
    val column = scope.columns[columnNumber]
    for (positionInColumn in 0 until column.size) {
      val period = column[positionInColumn]
      val x = period.x + scrollXOffset
      val y = period.y + scrollYOffset
      when {
        x + period.width < scope.timetableViewPortLeft -> break
        x > constraints.maxWidth -> break
        y + period.height < scope.timetableViewPortTop -> continue
        y > constraints.maxHeight -> break
      }
      visibleItems.add(
        VisibleItem(
          x,
          y,
          TIMETABLE_Z,
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
        z = COLUMN_HEADER_Z,
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
  for (timeLabel in scope.timeLabels) {
    val y = timeLabel.y + scrollYOffset
    when {
      y + timeLabel.height < 0 -> continue
      y > constraints.maxHeight -> break
    }
    visibleItems.add(
      VisibleItem(
        x = timeLabel.x,
        y = y,
        z = TIME_COLUMN_Z,
        columnNumber = -1,
        placeable = measure(
          timeLabel.positionInItemProvider,
          Constraints(
            minWidth = timeLabel.width,
            maxWidth = timeLabel.width,
            minHeight = 0,
            maxHeight = constraints.maxHeight,
          )
        ).first()
      )
    )
  }
  scope.columnHeaderBackground?.let {
    visibleItems.add(
      VisibleItem(
        it.x,
        it.y,
        COLUMN_HEADER_BACKGROUND_Z,
        -1,
        measure(
          it.positionInItemProvider,
          Constraints(
            minWidth = constraints.maxWidth,
            maxWidth = constraints.maxWidth,
            minHeight = it.height,
            maxHeight = it.height,
          )
        ).first(),
      )
    )
  }
  scope.timeColumnBackground?.let {
    visibleItems.add(
      VisibleItem(
        it.x,
        it.y,
        TIME_COLUMN_BACKGROUND_Z,
        -1,
        measure(
          it.positionInItemProvider,
          Constraints(
            minWidth = it.width,
            maxWidth = it.width,
            minHeight = constraints.maxHeight,
            maxHeight = constraints.maxHeight,
          )
        ).first(),
      )
    )
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

private class VisibleItem(
  val x: Int,
  val y: Int,
  val z: Float,
  val columnNumber: Int,
  val placeable: Placeable,
  val isPeriod: Boolean = false,
)
