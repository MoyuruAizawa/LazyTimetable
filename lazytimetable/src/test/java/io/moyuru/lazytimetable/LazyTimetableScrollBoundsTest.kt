package io.moyuru.lazytimetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
class LazyTimetableScrollBoundsTest {
  @get:Rule
  val compose = createComposeRule()

  @Test
  fun smallContentDoesNotMoveInAnyDirection() {
    val state = showTimetable(columnCount = 2)
    val item = compose.onNodeWithTag("item0")
    val before = item.fetchSemanticsNode().boundsInRoot
    val timetable = compose.onNodeWithTag("timetable")
    listOf<androidx.compose.ui.test.TouchInjectionScope.() -> Unit>(
      { swipeLeft() },
      { swipeUp() },
      { swipeRight() },
      { swipeDown() },
    ).forEach { swipe ->
      timetable.performTouchInput(swipe)
      compose.runOnIdle {
        assertEquals(0, state.scrollX)
        assertEquals(0, state.scrollY)
      }
      assertEquals(before, item.fetchSemanticsNode().boundsInRoot)
    }
  }

  @Test
  fun emptyLastColumnStillAllowsScrollingToTheRightEdge() {
    val state = showTimetable(columnCount = 6, emptyLastColumn = true)
    val timetable = compose.onNodeWithTag("timetable")
    timetable.performTouchInput { swipeLeft() }
    compose.runOnIdle { assertTrue(state.scrollX > 0) }
    repeat(10) { timetable.performTouchInput { swipeLeft() } }
    compose.runOnIdle {
      val expected = with(compose.density) { (6 * 100 + 5 * 10 - 300).dp.roundToPx() }
      assertEquals(expected, state.scrollX)
    }
  }

  @Test
  fun invalidPositiveMinimumConsumesNoScroll() {
    val state = LazyTimetableState().apply {
      scrollHorizontalMin = 100
      scrollVerticalMin = 100
    }
    assertEquals(Offset.Zero, state.scroll(-20f, -20f))
    assertEquals(0, state.scrollX)
    assertEquals(0, state.scrollY)
  }

  private fun showTimetable(columnCount: Int, emptyLastColumn: Boolean = false): LazyTimetableState {
    val state = LazyTimetableState()
    compose.setContent {
      LazyTimetable(
        modifier = Modifier.size(300.dp).testTag("timetable"),
        state = state,
        horizontalSpacing = 10.dp,
        columnWidth = 100.dp,
        heightPerMinute = 1.dp,
        columnHeaderHeight = 0.dp,
        columnHeaderColor = Color.Transparent,
        timeColumnWidth = 0.dp,
        timeColumnColor = Color.Transparent,
        baseEpochSec = 0,
        timeLabel = { Box(Modifier) },
      ) {
        repeat(columnCount) { index ->
          column(header = { Box(Modifier) }) {
            if (!emptyLastColumn || index != columnCount - 1) {
              item(60 * 60) { Box(Modifier.fillMaxSize().testTag("item$index")) }
            }
          }
        }
      }
    }
    compose.waitForIdle()
    return state
  }
}
