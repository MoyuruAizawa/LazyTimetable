package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable

internal class Period(
  val columnNumber: Int,
  val positionInColumn: Int,
  val positionInItemProvider: Int,
  val width: Int,
  val height: Int,
  val x: Int,
  val y: Int,
  val content: @Composable LazyTimetableItemScope.() -> Unit
)
