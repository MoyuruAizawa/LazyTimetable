package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyTimetable(
  modifier: Modifier = Modifier,
  listState: LazyTimetableState = rememberLazyTimetableState(),
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  content: LazyTimetableScope.() -> Unit
) {
  val density = LocalDensity.current
  val columnWidthPx = remember(columnWidth) { density.run { columnWidth.roundToPx() } }
  val heightPerMinutePx = remember(heightPerMinute) { density.run { heightPerMinute.roundToPx() } }
  val columnHeaderHeightPx = remember(columnHeaderHeight) { density.run { columnHeaderHeight.roundToPx() } }
  val scope = remember(
    columnWidthPx,
    heightPerMinutePx,
    columnHeaderHeightPx
  ) { LazyTimetableScopeImpl(columnWidthPx, heightPerMinutePx, columnHeaderHeightPx) }
  content(scope)
  val itemProvider = LazyTimetableItemProvider(scope)
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

