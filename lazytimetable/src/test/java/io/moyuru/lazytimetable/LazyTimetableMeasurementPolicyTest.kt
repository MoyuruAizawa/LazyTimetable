package io.moyuru.lazytimetable

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class LazyTimetableMeasurementPolicyTest {
  private fun column(vararg durations: Int): List<Period> {
    val scope = LazyTimetableScopeImpl(
      density = Density(1f),
      columnWidth = 100.dp,
      heightPerMinute = 1.dp,
      columnHeaderHeight = 40.dp,
      timeColumnWidth = 40.dp,
      horizontalSpacing = 0.dp,
      baseEpochSec = 0,
    )
    scope.column(header = {}) { durations.forEach { item(it) {} } }
    return scope.columns.single()
  }

  @Test
  fun emptyColumnStartsAtZero() {
    assertEquals(0, firstPeriodIndexReachingViewportTop(column(), 0))
  }

  @Test
  fun allPeriodsAboveViewportReturnSize() {
    val column = column(60, 60, 60)
    assertEquals(column.size, firstPeriodIndexReachingViewportTop(column, -4))
  }

  @Test
  fun periodCrossingTopIsIncluded() {
    assertEquals(1, firstPeriodIndexReachingViewportTop(column(120, 120, 120), -3))
  }

  @Test
  fun periodEndingAtTopIsIncluded() {
    assertEquals(0, firstPeriodIndexReachingViewportTop(column(60, 60), -1))
  }

  @Test
  fun equalBottomsReturnFirstMatchingPeriod() {
    assertEquals(1, firstPeriodIndexReachingViewportTop(column(60, 60, 0, 0, 60), -2))
  }
}
