package io.moyuru.lazytimetable

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class LazyTimetableStateTest {
  @Test
  fun smallHorizontalDeltasAccumulate() {
    val state = state()
    repeat(10) { state.scroll(-0.4f, 0f) }
    assertEquals(4, state.scrollX)
  }

  @Test
  fun smallVerticalDeltasAccumulate() {
    val state = state()
    repeat(10) { state.scroll(0f, -0.4f) }
    assertEquals(4, state.scrollY)
  }

  @Test
  fun fractionalPartsOfLargerDeltasAccumulate() {
    val state = state()
    repeat(10) { state.scroll(-1.7f, -1.7f) }
    assertEquals(17, state.scrollX)
    assertEquals(17, state.scrollY)
  }

  @Test
  fun consumesFractionalMovementWithinBounds() {
    assertOffset(-0.4f, -0.4f, state().scroll(-0.4f, -0.4f))
  }

  @Test
  fun stopsAtMinimumAndConsumesOnlyAvailableMovement() {
    val state = state(-10)
    assertOffset(-10f, -10f, state.scroll(-15.5f, -15.5f))
    assertEquals(10, state.scrollX)
    assertEquals(10, state.scrollY)
    assertOffset(0f, 0f, state.scroll(-1f, -1f))
    assertEquals(10, state.scrollX)
    assertEquals(10, state.scrollY)
  }

  @Test
  fun stopsAtMaximum() {
    val state = state()
    assertOffset(0f, 0f, state.scroll(5f, 5f))
    assertEquals(0, state.scrollX)
    assertEquals(0, state.scrollY)
  }

  @Test
  fun reversingDirectionReturnsToOrigin() {
    val state = state()
    repeat(3) { state.scroll(-0.4f, -0.4f) }
    repeat(3) { state.scroll(0.4f, 0.4f) }
    assertEquals(0, state.scrollX)
    assertEquals(0, state.scrollY)
  }

  @Test
  fun reachingBoundsClearsRemainders() {
    val state = state(-10)
    state.scroll(-0.4f, -0.4f)
    assertOffset(-9.6f, -9.6f, state.scroll(-20f, -20f))
    assertOffset(0.4f, 0.4f, state.scroll(0.4f, 0.4f))
    assertOffset(9.6f, 9.6f, state.scroll(20f, 20f))
    assertOffset(-0.4f, -0.4f, state.scroll(-0.4f, -0.4f))
  }

  private fun state(min: Int = -1000) = LazyTimetableState().apply {
    scrollHorizontalMin = min
    scrollVerticalMin = min
  }

  private fun assertOffset(x: Float, y: Float, actual: Offset) {
    assertEquals(x, actual.x, 1e-4f)
    assertEquals(y, actual.y, 1e-4f)
  }
}
