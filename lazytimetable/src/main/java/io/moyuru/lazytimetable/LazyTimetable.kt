package io.moyuru.lazytimetable

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.moyuru.lazytimetable.columnheader.LazyColumnHeader
import io.moyuru.lazytimetable.timecolumn.LazyTimeColumn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * A composable that creates a lazy-loaded, scrollable timetable layout.
 *
 * @param state State object that provides scroll state information
 * @param horizontalSpacing Horizontal spacing between columns
 * @param contentPadding Padding around the entire content area
 * @param columnWidth Width of each column in the timetable
 * @param heightPerMinute Height allocated per minute of duration
 * @param columnHeaderHeight Height of the column headers
 * @param columnHeaderColor Background color of the column headers
 * @param timeColumnWidth Width of the time column (left side time labels)
 * @param timeColumnColor Background color of the time column (left side time labels)
 * @param baseEpochSec Base timestamp in epoch seconds used as the reference point for time calculations
 * @param timeLabel Composable function for rendering time labels, receives epoch seconds as parameter
 * @param content DSL block for defining the timetable structure using [LazyTimetableScope]
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyTimetable(
  modifier: Modifier = Modifier,
  state: LazyTimetableState = rememberLazyTimetableState(),
  horizontalSpacing: Dp = 0.dp,
  contentPadding: PaddingValues = PaddingValues(),
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  columnHeaderColor: Color,
  timeColumnWidth: Dp,
  timeColumnColor: Color,
  baseEpochSec: Long,
  timeLabel: @Composable (Long) -> Unit,
  content: LazyTimetableScope.() -> Unit
) {
  LazyTimetable(
    modifier,
    state,
    horizontalSpacing,
    contentPadding,
    columnWidth,
    heightPerMinute,
    columnHeaderHeight,
    SolidColor(columnHeaderColor),
    timeColumnWidth,
    SolidColor(timeColumnColor),
    baseEpochSec,
    timeLabel,
    content,
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyTimetable(
  modifier: Modifier = Modifier,
  timetableState: LazyTimetableState = rememberLazyTimetableState(),
  horizontalSpacing: Dp = 0.dp,
  contentPadding: PaddingValues = PaddingValues(),
  columnWidth: Dp,
  heightPerMinute: Dp,
  columnHeaderHeight: Dp,
  columnHeaderColor: Brush,
  timeColumnWidth: Dp,
  timeColumnColor: Brush,
  baseEpochSec: Long,
  timeLabel: @Composable (Long) -> Unit,
  content: LazyTimetableScope.() -> Unit
) {
  val density = LocalDensity.current
  val scope = remember(
    content,
    timeLabel,
    columnWidth,
    heightPerMinute,
    columnHeaderHeight,
    timeColumnWidth,
    horizontalSpacing,
    baseEpochSec,
  ) {
    LazyTimetableScopeImpl(
      density = density,
      columnWidth = columnWidth,
      heightPerMinute = heightPerMinute,
      columnHeaderHeight = columnHeaderHeight,
      timeColumnWidth = timeColumnWidth,
      horizontalSpacing = horizontalSpacing,
      baseEpochSec = baseEpochSec,
    )
  }
  content(scope)
  scope.timeLabel(timeLabel)
  val coroutineScope = rememberCoroutineScope()
  Column(
    modifier = modifier,
  ) {
    LazyColumnHeader(
      timeColumnWidth,
      contentPadding,
      timetableState,
      scope,
      modifier = Modifier
        .background(columnHeaderColor)
    )
    Row(
      modifier = Modifier.weight(1f)
    ) {
      LazyTimeColumn(
        contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
        timetableState,
        scope,
        modifier = Modifier
          .background(timeColumnColor)
      )
      LazyLayout(
        itemProvider = { LazyTimetableItemProvider(scope) },
        measurePolicy = lazyTimetableMeasurementPolicy(contentPadding, timetableState, scope),
        modifier = Modifier
          .weight(1f)
          .clipToBounds()
          .pointerInput(Unit) {
            val velocityTracker = VelocityTracker()
            detectDragGestures(
              onDragStart = { offset ->
                velocityTracker.resetTracking()
                coroutineScope.launch { timetableState.stopFling() }
              },
              onDrag = { change, dragAmount ->
                velocityTracker.addPosition(change.uptimeMillis, change.position)
                if (timetableState.canScroll(dragAmount.x, dragAmount.y))
                  change.consume()
                timetableState.scroll(dragAmount.x, dragAmount.y)
              },
              onDragCancel = {
                velocityTracker.resetTracking()
              },
              onDragEnd = {
                val velocity = velocityTracker.calculateVelocity()
                coroutineScope.launch {
                  timetableState.fling(velocity.x, velocity.y)
                }
                velocityTracker.resetTracking()
              }
            )
          },
      )
    }
  }
}

@SuppressLint("NewApi")
@Preview
@Composable
private fun PreviewLazyTimetable() {
  LazyTimetable(
    horizontalSpacing = 4.dp,
    columnWidth = 100.dp,
    heightPerMinute = 3.dp,
    columnHeaderHeight = 80.dp,
    columnHeaderColor = Color.Black,
    timeColumnWidth = 100.dp,
    timeColumnColor = Color.Black,
    baseEpochSec = 1767225600,
    timeLabel = {
      val text = remember(it) {
        Instant.ofEpochSecond(it)
          .atZone(ZoneId.of("UTC"))
          .format(DateTimeFormatter.ofPattern("HH:mm"))
      }
      Text(
        text,
        color = Color.White,
        textAlign = TextAlign.Center,
      )
    },
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black),
  ) {

    listOf(
      listOf("Nicky Romero", "EMPTY", "Alesso", "Steve Aoki", "Afrojack", "David Guetta"),
      listOf("Julian Jordan", "Third Party", "EMPTY", "Matisse & Sadko", "DubVision", "Martin Garrix"),
      listOf("Fairlane", "Virtual Riot", "Crankdat", "EMPTY", "Skrillex", "DJ Snake"),
      listOf("R3HAB", "W&W", "KSHMR", "Steve Aoki", "EMPTY", "Dimitri Vegas & Like Mike"),
      listOf("CRUNKZ", "Justin Mylo", "Brooks", "Mesto", "Mike Williams", "EMPTY"),
      listOf("Blasterjaxx", "Dyro", "KAAZE", "Maddix", "EMPTY", "Hardwell", "EMPTY"),
    )
      .forEachIndexed { i, djList ->
        column(
          header = {
            Box(contentAlignment = Alignment.Center) {
              Text(
                "Stage $i",
                color = Color.White
              )
            }
          }
        ) {
          djList.forEachIndexed { j, dj ->
            item(durationSec = if (dj == "EMPTY") 60 * 10 else 60 * 60) {
              if (dj == "EMPTY") {
                Spacer(Modifier)
              } else {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(Color.White)
                    .padding(8.dp)
                ) {
                  Text(text = dj)
                }
              }
            }
          }
        }
      }
  }
}
