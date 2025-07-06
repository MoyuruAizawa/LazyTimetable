package io.moyuru.lazytimetable.columnheader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.unit.Constraints
import io.moyuru.lazytimetable.LazyTimetableScopeImpl
import io.moyuru.lazytimetable.LazyTimetableState
import io.moyuru.lazytimetable.VisibleItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun lazyColumnHeaderMeasurementPolicy(
  state: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
) : LazyLayoutMeasureScope.(Constraints) -> MeasureResult = { constraints ->
  val visibleItems = mutableListOf<VisibleItem>()
  val previousVisibleColumn = (state.firstVisibleColumnNumber - 1).coerceAtLeast(0)
  val scrollXOffset = state.scrollXOffset

  for (columnNumber in previousVisibleColumn until scope.columnCount) {
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
        z = 0f,
        columnNumber = columnNumber,
        placeable = measure(
          columnNumber,
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
  layout(constraints.maxWidth, visibleItems.maxOf { it.placeable.height + it.y }) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y, it.z)
    }
  }
}
