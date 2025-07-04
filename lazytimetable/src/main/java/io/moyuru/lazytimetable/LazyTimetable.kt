package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyTimetable(
  modifier: Modifier = Modifier,
  listState: LazyTimetableState = rememberLazyTimetableState(),
  horizontalSpacing: Dp = 0.dp,
  contentPadding: PaddingValues = PaddingValues(),
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  columnHeaderColor: Color,
  timeColumnWidth: Dp,
  timeColumnColor: Color,
  baseEpochSec: Long,
  timeLabel: @Composable (Long) -> Unit,
  content: LazyTimetableScope.() -> Unit
) {
  val density = LocalDensity.current
  val scope = remember(
    horizontalSpacing,
    contentPadding,
    columnWidth,
    heightPerMinute,
    columnHeaderHeight,
    timeColumnWidth,
  ) {
    LazyTimetableScopeImpl(
      density = density,
      columnWidth = columnWidth,
      heightPerMinute = heightPerMinute,
      columnHeaderHeight = columnHeaderHeight,
      timeColumnWidth = timeColumnWidth,
      horizontalSpacing = horizontalSpacing,
      contentPadding = contentPadding,
      baseEpochSec = baseEpochSec,
      columnHeaderColor = columnHeaderColor,
      timeColumnColor = timeColumnColor,
    )
  }
  content(scope)
  scope.background()
  scope.timeLabel(timeLabel)
  val itemProvider = remember(
    horizontalSpacing,
    contentPadding,
    columnWidth,
    heightPerMinute,
    columnHeaderHeight,
  ) {
    LazyTimetableItemProvider(scope)
  }
  val coroutineScope = rememberCoroutineScope()

  LazyLayout(
    itemProvider = { itemProvider },
    measurePolicy = rememberMeasurementPolicy(listState, scope),
    modifier = modifier
      .pointerInput(Unit) {
        val velocityTracker = VelocityTracker()
        detectDragGestures(
          onDragStart = { offset ->
            velocityTracker.resetTracking()
            coroutineScope.launch { listState.stopFling() }
          },
          onDrag = { change, dragAmount ->
            velocityTracker.addPosition(change.uptimeMillis, change.position)
            listState.scroll(dragAmount.x, dragAmount.y)
            change.consume()
          },
          onDragCancel = {
            velocityTracker.resetTracking()
          },
          onDragEnd = {
            val velocity = velocityTracker.calculateVelocity()
            coroutineScope.launch {
              listState.fling(velocity.x, velocity.y)
            }
            velocityTracker.resetTracking()
          }
        )
      },
  )
}

