package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt

@Composable
fun rememberLazyTimetableState() = rememberSaveable(
  saver = LazyTimetableState.SAVER,
) {
  LazyTimetableState()
}

@Stable
class LazyTimetableState internal constructor() {
  internal var scrollYOffset by mutableIntStateOf(0)
  internal var scrollXOffset by mutableIntStateOf(0)
  val scrollY get() = -scrollYOffset
  val scrollX get() = -scrollXOffset
  var firstVisibleColumnNumber by mutableIntStateOf(-1)
    internal set
  var lastVisibleColumnNumber by mutableIntStateOf(-1)
    internal set
  internal var scrollVerticalMin = 0
  internal val scrollVerticalMax = 0
  internal var scrollHorizontalMin = 0
  internal val scrollHorizontalMax = 0
  private var scrollXRemainder = 0f
  private var scrollYRemainder = 0f

  internal fun scroll(deltaX: Float, deltaY: Float): Offset {
    val oldX = scrollXOffset + scrollXRemainder
    val oldY = scrollYOffset + scrollYRemainder
    val newX = (oldX + deltaX)
      .coerceAtMost(scrollHorizontalMax.toFloat())
      .coerceAtLeast(scrollHorizontalMin.toFloat())
    val newY = (oldY + deltaY)
      .coerceAtMost(scrollVerticalMax.toFloat())
      .coerceAtLeast(scrollVerticalMin.toFloat())
    scrollXOffset = newX.roundToInt()
    scrollYOffset = newY.roundToInt()
    // Keep consumed subpixel movement until it contributes to an integer offset.
    scrollXRemainder = newX - scrollXOffset
    scrollYRemainder = newY - scrollYOffset
    return Offset(
      (newX - oldX),
      (newY - oldY),
    )
  }

  companion object {
    internal val SAVER: Saver<LazyTimetableState, *> = listSaver(
      save = {
        listOf(
          it.scrollYOffset,
          it.scrollXOffset,
          it.firstVisibleColumnNumber,
          it.lastVisibleColumnNumber,
        )
      },
      restore = {
        LazyTimetableState().apply {
          scrollYOffset = it[0]
          scrollXOffset = it[1]
          firstVisibleColumnNumber = it[2]
          lastVisibleColumnNumber = it[3]
        }
      },
    )
  }
}
