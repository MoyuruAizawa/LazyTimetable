package io.moyuru.lazytimetable.timecolumn

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutMeasureScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import io.moyuru.lazytimetable.LazyTimetableScopeImpl
import io.moyuru.lazytimetable.LazyTimetableState
import io.moyuru.lazytimetable.VisibleItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun lazyTimeColumnMeasurementPolicy(
  paddingLeft: Dp,
  state: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
): LazyLayoutMeasureScope.(Constraints) -> MeasureResult = { constraints ->
  val visibleItems = mutableListOf<VisibleItem>()
  val scrollYOffset = state.scrollYOffset
  val paddingLeftPx = paddingLeft.roundToPx()
  for (index in 0 until scope.timeLabels.size) {
    val timeLabel = scope.timeLabels[index]
    val y = timeLabel.y + scrollYOffset
    when {
      y + timeLabel.height < 0 -> continue
      y > constraints.maxHeight -> break
    }
    visibleItems.add(
      VisibleItem(
        x = timeLabel.x + paddingLeftPx,
        y = y,
        z = 0f,
        columnNumber = null,
        placeable = measure(
          index,
          Constraints(
            minWidth = timeLabel.width,
            maxWidth = timeLabel.width,
            minHeight = 0,
            maxHeight = constraints.maxHeight,
          ),
        ).first(),
      ),
    )
  }
  layout(visibleItems.maxOf { it.placeable.width }, constraints.maxHeight) {
    visibleItems.forEach {
      it.placeable.place(it.x, it.y, it.z)
    }
  }
}
