package io.moyuru.lazytimetable

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class LazyTimetableEmptyDataTest {
  @get:Rule
  val compose = createComposeRule()

  @Composable
  private fun Timetable(timeColumnWidth: Dp = 40.dp, content: LazyTimetableScope.() -> Unit) {
    LazyTimetable(
      modifier = Modifier.size(200.dp),
      columnWidth = 100.dp,
      heightPerMinute = 1.dp,
      columnHeaderHeight = 40.dp,
      columnHeaderColor = Color.White,
      timeColumnWidth = timeColumnWidth,
      timeColumnColor = Color.White,
      baseEpochSec = 0,
      timeLabel = { BasicText("Time") },
      content = content,
    )
  }

  @Test
  fun noColumnsDoesNotCrash() {
    compose.setContent { Timetable {} }
    compose.waitForIdle()
  }

  @Test
  fun emptyColumnDisplaysHeader() {
    compose.setContent {
      Timetable { column(header = { BasicText("Stage 0") }) {} }
    }
    compose.onNodeWithText("Stage 0").assertIsDisplayed()
  }

  @Test
  fun emptyToPopulatedDoesNotCrash() {
    val populated = mutableStateOf(false)
    compose.setContent {
      val showItem = populated.value
      Timetable {
        if (showItem) {
          column(header = { BasicText("Stage 0") }) {
            item(3600) { BasicText("Event") }
          }
        }
      }
    }
    compose.waitForIdle()
    compose.runOnIdle { populated.value = true }
    compose.onNodeWithText("Event").assertIsDisplayed()
  }

  @Test
  fun populatedToEmptyDoesNotCrash() {
    val populated = mutableStateOf(true)
    compose.setContent {
      val showItem = populated.value
      Timetable {
        if (showItem) {
          column(header = { BasicText("Stage 0") }) {
            item(3600) { BasicText("Event") }
          }
        }
      }
    }
    compose.onNodeWithText("Event").assertIsDisplayed()
    compose.runOnIdle { populated.value = false }
    compose.waitForIdle()
  }

  @Test
  fun noVisibleHeadersDoesNotCrash() {
    compose.setContent {
      Timetable(timeColumnWidth = 300.dp) {
        column(header = { BasicText("Stage 0") }) {
          item(3600) { BasicText("Event") }
        }
      }
    }
    compose.waitForIdle()
  }
}
