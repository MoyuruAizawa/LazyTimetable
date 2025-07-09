package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.exp

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
  private var flingJobX: Job? = null
  private var flingJobY: Job? = null

  internal fun canScroll(deltaX: Float, deltaY: Float): Boolean =
    (scrollHorizontalMax > scrollXOffset + deltaX && scrollXOffset + deltaX < scrollHorizontalMin) ||
      (scrollVerticalMax > scrollYOffset + deltaY && scrollYOffset + deltaY < scrollVerticalMin)

  internal fun scroll(deltaX: Float, deltaY: Float) {
    scrollXOffset = (scrollXOffset + deltaX.toInt())
      .coerceAtMost(scrollHorizontalMax)
      .coerceAtLeast(scrollHorizontalMin)
    scrollYOffset = (scrollYOffset + deltaY.toInt())
      .coerceAtMost(scrollVerticalMax)
      .coerceAtLeast(scrollVerticalMin)
  }

  internal suspend fun fling(velocityX: Float, velocityY: Float) {
    coroutineScope {
      flingJobX?.cancel()
      flingJobY?.cancel()

      flingJobX = async {
        animateExponentialDecay(
          initialValue = scrollXOffset.toFloat(),
          initialVelocity = velocityX,
        ) { value ->
          scrollXOffset = value
            .toInt()
            .coerceAtMost(scrollHorizontalMax)
            .coerceAtLeast(scrollHorizontalMin)
        }
      }

      flingJobY = async {
        animateExponentialDecay(
          initialValue = scrollYOffset.toFloat(),
          initialVelocity = velocityY,
        ) { value ->
          scrollYOffset = value
            .toInt()
            .coerceAtMost(scrollVerticalMax)
            .coerceAtLeast(scrollVerticalMin)
        }
      }

      flingJobX?.join()
      flingJobY?.join()
    }
  }

  internal fun stopFling() {
    flingJobX?.cancel()
    flingJobY?.cancel()
    flingJobX = null
    flingJobY = null
  }

  private suspend fun animateExponentialDecay(
    initialValue: Float,
    initialVelocity: Float,
    onUpdate: (Float) -> Unit,
  ) {
    val decayConstant = -4.2f
    val threshold = 0.1f

    var currentValue = initialValue
    var currentVelocity = initialVelocity
    val frameTimeMs = 1000 / 60L
    val frameTimeSec = frameTimeMs / 1000f
    while (abs(currentVelocity) > threshold) {
      currentVelocity *= exp(decayConstant * frameTimeSec)
      currentValue += currentVelocity * frameTimeSec

      onUpdate(currentValue)
      delay(frameTimeMs)
    }
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
