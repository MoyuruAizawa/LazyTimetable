package io.moyuru.lazytimetable

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
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

  @Test
  fun fractionalMinutesDoNotAccumulateRoundingError() {
    val scope = scope(1.dp)
    scope.column(header = {}) {
      repeat(41) { item(90) {} }
    }
    scope.timeLabel {}

    scope.columns.single().forEachIndexed { index, period ->
      assertEquals((index * 1.5f).roundToInt(), period.y)
    }
    assertEquals(60, scope.columns.single()[39].bottom)
    assertEquals(60, scope.columns.single()[40].y)
    assertEquals(scope.columns.single()[40].y, scope.timeLabels[1].y)
  }

  @Test
  fun fractionalHeightPerMinuteIsPreserved() {
    val scope = scope(1.5.dp)
    scope.column(header = {}) {
      repeat(3) { item(3600) {} }
    }
    scope.timeLabel {}

    scope.columns.single().forEachIndexed { index, period ->
      assertEquals(90 * index, period.y)
      assertEquals(90, period.height)
      assertEquals(90 * index, scope.timeLabels[index].y)
      assertEquals(90, scope.timeLabels[index].height)
    }
  }

  @Test
  fun mixedDurationsStayContiguousAndUseTheSameScaleAsLabels() {
    val scope = scope(1.5.dp)
    val durations = listOf(45, 90, 125, 3600, 17)
    listOf(durations, durations.reversed()).forEach { columnDurations ->
      scope.column(header = {}) {
        columnDurations.forEach { item(it) {} }
      }
    }
    scope.timeLabel {}

    scope.columns.forEach { column ->
      column.forEach { period ->
        assertEquals(expectedY(period.startAtSec), period.y)
        assertEquals(expectedY(period.endAtSec), period.bottom)
        assertTrue(period.height >= 0)
      }
      column.zipWithNext().forEach { (previous, next) ->
        assertEquals(previous.bottom, next.y)
      }
    }
    scope.timeLabels.forEachIndexed { index, label ->
      assertEquals(expectedY(BASE + index * 3600L), label.y)
      assertEquals(90, label.height)
    }
  }

  private fun expectedY(epochSec: Long): Int = ((epochSec - BASE) * 1.5 / 60).roundToInt()

  private fun scope(heightPerMinute: Dp) = LazyTimetableScopeImpl(
    density = Density(1f),
    columnWidth = 100.dp,
    heightPerMinute = heightPerMinute,
    columnHeaderHeight = 10.dp,
    timeColumnWidth = 10.dp,
    horizontalSpacing = 0.dp,
    baseEpochSec = BASE,
  )

  private companion object {
    const val BASE = 1767225600L
  }
}
