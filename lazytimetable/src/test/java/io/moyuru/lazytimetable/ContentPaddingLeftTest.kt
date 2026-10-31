package io.moyuru.lazytimetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@Config(sdk = [35])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ContentPaddingLeftTest {
  @get:Rule
  val compose = createComposeRule()

  @Test
  fun leftPaddingAlignsHeadersAndItems() {
    showTimetable(24.dp)
    assertColumnAlignment(84.dp)
  }

  @Test
  fun leftPaddingDoesNotClipTimeLabel() {
    showTimetable(24.dp)
    val label = compose.onNodeWithTag("label0")
    val unclipped = label.getUnclippedBoundsInRoot()
    assertEquals(24.dp, unclipped.left)
    assertEquals(60.dp, unclipped.right - unclipped.left)
    val clipped = label.getBoundsInRoot()
    assertEquals(60.dp, clipped.right - clipped.left)
  }

  @Test
  fun zeroLeftPaddingKeepsHeadersAndItemsAligned() {
    showTimetable(0.dp)
    assertColumnAlignment(60.dp)
  }

  private fun assertColumnAlignment(firstColumnLeft: Dp) {
    repeat(2) { column ->
      val expected = firstColumnLeft + 100.dp * column
      assertEquals(expected, compose.onNodeWithTag("header$column").getUnclippedBoundsInRoot().left)
      assertEquals(expected, compose.onNodeWithTag("item$column").getUnclippedBoundsInRoot().left)
    }
  }

  private fun showTimetable(leftPadding: Dp) {
    compose.setContent {
      LazyTimetable(
        modifier = Modifier.size(320.dp, 240.dp),
        contentPadding = PaddingValues(start = leftPadding),
        columnWidth = 100.dp,
        heightPerMinute = 1.dp,
        columnHeaderHeight = 40.dp,
        columnHeaderColor = Color.Gray,
        timeColumnWidth = 60.dp,
        timeColumnColor = Color.Gray,
        baseEpochSec = 0L,
        timeLabel = { epochSec ->
          Box(Modifier.fillMaxWidth().height(20.dp).testTag("label$epochSec"))
        },
      ) {
        repeat(2) { column ->
          column(header = { Box(Modifier.fillMaxSize().testTag("header$column")) }) {
            item(durationSec = 3600) {
              Box(Modifier.fillMaxSize().testTag("item$column"))
            }
          }
        }
      }
    }
  }
}
