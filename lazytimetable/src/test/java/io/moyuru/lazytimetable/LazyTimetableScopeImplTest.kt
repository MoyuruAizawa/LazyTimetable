package io.moyuru.lazytimetable

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LazyTimetableScopeImplTest {
  private fun scope() = LazyTimetableScopeImpl(
    density = Density(1f),
    columnWidth = 100.dp,
    heightPerMinute = 1.dp,
    columnHeaderHeight = 40.dp,
    timeColumnWidth = 40.dp,
    horizontalSpacing = 0.dp,
    baseEpochSec = 0,
  )

  @Test
  fun noColumnsProducesNoTimeLabels() {
    val scope = scope()
    scope.timeLabel { }
    assertTrue(scope.timeLabels.isEmpty())
  }

  @Test
  fun emptyColumnsKeepHeadersWithoutTimeLabels() {
    val scope = scope()
    repeat(2) { scope.column(header = {}) {} }
    scope.timeLabel { }
    assertTrue(scope.timeLabels.isEmpty())
    assertEquals(2, scope.columnHeaders.size)
  }

  @Test
  fun twoHoursProduceTwoTimeLabels() {
    val scope = scope()
    scope.column(header = {}) {
      repeat(2) { item(3600) {} }
    }
    scope.timeLabel { }
    assertEquals(2, scope.timeLabels.size)
  }
}
