package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable

internal interface VirtualMeasuredItem {
  val positionInItemProvider: Int
  val width: Int
  val height: Int
  val x: Int
  val y: Int
  val content: @Composable () -> Unit

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
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem

internal class ColumnHeader(
  override val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem

internal data class TimeLabel(
  override val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem

internal class ColumnHeaderBackground(
  override val positionInItemProvider: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem {
  // MeasurementPolicy ignores this value.
  override val width: Int = Int.MAX_VALUE
}

internal class TimeColumnBackground(
  override val positionInItemProvider: Int,
  override val width: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem {
  // MeasurementPolicy ignores this value.
  override val height: Int = Int.MAX_VALUE
}
