package io.moyuru.lazytimetable

internal interface VirtualMeasuredItem {
  val positionInItemProvider: Int
  val width: Int
  val height: Int
  val x: Int
  val y: Int

  val left get() = x
  val top get() = y
  val right get() = x + width
  val bottom get() = y + height
}

internal class Period(
  val columnNumber: Int,
  val startAtSec: Long,
  val endAtSec: Long,
  override val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int
) : VirtualMeasuredItem

internal class ColumnHeader(
  override val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int
) : VirtualMeasuredItem

internal data class TimeLabel(
  override val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int
) : VirtualMeasuredItem

internal class LeftTopCorner(
  override val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int
) : VirtualMeasuredItem
