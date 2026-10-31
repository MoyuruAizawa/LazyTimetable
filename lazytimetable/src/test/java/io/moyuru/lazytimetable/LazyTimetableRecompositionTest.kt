package io.moyuru.lazytimetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.model.MultipleFailureException
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class LazyTimetableRecompositionTest {
  @get:Rule
  val compose = createComposeRule()

  @Test
  fun recomposition_doesNotDuplicateItems() {
    var padding by mutableStateOf(PaddingValues())
    compose.setContent {
      Timetable(padding = padding) {
        repeat(2) {
          column(header = { Box(Modifier.testTag("header")) }) {
            repeat(2) {
              item(durationSec = 3600) { Box(Modifier.testTag("item")) }
            }
          }
        }
      }
    }
    assertCounts()
    for (inset in listOf(8, 16)) {
      compose.runOnIdle { padding = PaddingValues(inset.dp) }
      compose.waitForIdle()
      assertCounts()
    }
  }

  private fun assertCounts() {
    val failures = mutableListOf<Throwable>()
    for ((tag, count) in listOf("item" to 4, "header" to 2, "label" to 2)) {
      try {
        compose.onAllNodesWithTag(tag).assertCountEquals(count)
      } catch (failure: AssertionError) {
        failures.add(failure)
      }
    }
    MultipleFailureException.assertEmpty(failures)
  }

  @Test
  fun stateReadInContent_rebuildsScope() {
    var count by mutableStateOf(1)
    compose.setContent {
      Timetable {
        column(header = { Box(Modifier) }) {
          repeat(count) {
            item(durationSec = 3600) { Box(Modifier.testTag("item")) }
          }
        }
      }
    }
    compose.onAllNodesWithTag("item").assertCountEquals(1)
    compose.runOnIdle { count = 3 }
    compose.onAllNodesWithTag("item").assertCountEquals(3)
  }

  @Test
  fun contentLambdaChange_rebuildsScope() {
    var entries by mutableStateOf(listOf("old"))
    compose.setContent {
      val currentEntries = entries
      Timetable {
        column(header = { Box(Modifier) }) {
          items(currentEntries, durationSec = { 3600 }) {
            BasicText(it, Modifier.testTag("item"))
          }
        }
      }
    }
    compose.onAllNodesWithTag("item").assertCountEquals(1)
    compose.onNodeWithText("old").assertExists()
    compose.runOnIdle { entries = listOf("new", "replacement") }
    compose.onAllNodesWithTag("item").assertCountEquals(2)
    compose.onNodeWithText("old").assertDoesNotExist()
    compose.onNodeWithText("new").assertExists()
    compose.onNodeWithText("replacement").assertExists()
  }

  @Test
  fun densityChange_remeasuresItems() {
    var density by mutableStateOf(1f)
    compose.setContent {
      CompositionLocalProvider(LocalDensity provides Density(density)) {
        Timetable {
          column(header = { Box(Modifier) }) {
            item(durationSec = 3600) { Box(Modifier.testTag("item")) }
          }
        }
      }
    }
    assertEquals(60, itemHeight())
    compose.runOnIdle { density = 2f }
    assertEquals(120, itemHeight())
  }

  private fun itemHeight(): Int = compose
    .onAllNodesWithTag("item")
    .fetchSemanticsNodes()
    .first()
    .size
    .height

  @Composable
  private fun Timetable(
    padding: PaddingValues = PaddingValues(),
    content: LazyTimetableScope.() -> Unit,
  ) {
    LazyTimetable(
      modifier = Modifier.fillMaxSize(),
      contentPadding = padding,
      columnWidth = 20.dp,
      heightPerMinute = 1.dp,
      columnHeaderHeight = 20.dp,
      columnHeaderColor = Color.White,
      timeColumnWidth = 20.dp,
      timeColumnColor = Color.White,
      baseEpochSec = 0L,
      timeLabel = { Box(Modifier.testTag("label")) },
      content = content,
    )
  }
}
