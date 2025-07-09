package io.moyuru.lazytimetable.columnheader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import io.moyuru.lazytimetable.LazyTimetableScopeImpl
import io.moyuru.lazytimetable.LazyTimetableState
import io.moyuru.lazytimetable.VisibleItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun lazyColumnHeaderMeasurementPolicy(
  timeColumnWidth: Dp,
  contentPadding: PaddingValues,
  state: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
): LazyLayoutMeasureScope.(Constraints) -> MeasureResult = { constraints ->
  val visibleItems = mutableListOf<VisibleItem>()
  val scrollXOffset = state.scrollXOffset
  val timeColumnWidthPx = timeColumnWidth.roundToPx()
  val paddingTop = contentPadding.calculateTopPadding().roundToPx()
  val paddingLeft = contentPadding.calculateLeftPadding(LayoutDirection.Ltr).roundToPx()

  for (index in 0 until scope.columnHeaders.size) {
    val columnHeader = scope.columnHeaders[index]
    val x = paddingLeft + timeColumnWidthPx + columnHeader.x + scrollXOffset
    when {
      x + columnHeader.width < 0 -> continue
      x > constraints.maxWidth -> break
    }
    visibleItems.add(
      VisibleItem(
        x = x,
        y = paddingTop + columnHeader.y,
        z = 0f,
        columnNumber = index,
        placeable = measure(
          index,
          Constraints(
            minWidth = columnHeader.width,
            maxWidth = columnHeader.width,
            minHeight = columnHeader.height,
            maxHeight = columnHeader.height,
          ),
        ).first(),
      ),
    )
  }
  layout(constraints.maxWidth, visibleItems.maxOf { it.placeable.height + it.y }) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y, it.z)
    }
  }
}
