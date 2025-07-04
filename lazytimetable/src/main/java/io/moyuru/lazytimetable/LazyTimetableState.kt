package io.moyuru.lazytimetable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.coroutineScope

@Composable
fun rememberLazyTimetableState() = rememberSaveable(
  saver = LazyTimetableState.SAVER
) {
  LazyTimetableState()
}

@Stable
class LazyTimetableState internal constructor() {
  var scrollYOffset by mutableIntStateOf(0)
    internal set
  var scrollXOffset by mutableIntStateOf(0)
    internal set
  var firstVisibleColumnNumber by mutableIntStateOf(-1)
    internal set
  var lastVisibleColumnNumber by mutableIntStateOf(-1)
    internal set
  internal var scrollVerticalMin = 0
  internal val scrollVerticalMax = 0
  internal var scrollHorizontalMin = 0
  internal val scrollHorizontalMax = 0
  private val scrollXAnimatable = Animatable(0f)
  private val scrollYAnimatable = Animatable(0f)

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
      scrollXAnimatable.snapTo(scrollXOffset.toFloat())
      scrollXAnimatable.animateDecay(
        initialVelocity = velocityX,
        animationSpec = exponentialDecay()
      ) {
        scrollXOffset = value.toInt()
          .coerceAtMost(scrollHorizontalMax)
          .coerceAtLeast(scrollHorizontalMin)
      }
    }
    coroutineScope {
      scrollYAnimatable.snapTo(scrollYOffset.toFloat())
      scrollYAnimatable.animateDecay(
        initialVelocity = velocityY,
        animationSpec = exponentialDecay()
      ) {
        scrollYOffset = value.toInt()
          .coerceAtMost(scrollVerticalMax)
          .coerceAtLeast(scrollVerticalMin)
      }
    }
  }

  internal suspend fun stopFling() {
    scrollXAnimatable.stop()
    scrollYAnimatable.stop()
  }

  companion object {
    val SAVER: Saver<LazyTimetableState, *> = listSaver(
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
      }
    )
  }
}
