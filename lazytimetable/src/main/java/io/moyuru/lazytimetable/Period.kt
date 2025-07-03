package io.moyuru.lazytimetable

internal class Period(
  val columnNumber: Int,
  val positionInColumn: Int,
  val positionInItemProvider: Int,
  val startAtSec: Long,
  val endAtSec: Long,
  val width: Int,
  val height: Int,
  val x: Int,
  val y: Int
)
