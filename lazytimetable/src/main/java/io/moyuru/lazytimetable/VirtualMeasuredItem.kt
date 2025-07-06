package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable

internal interface VirtualMeasuredItem {
  val width: Int
  val height: Int
  val x: Int
  val y: Int
  val content: @Composable () -> Unit

  val bottom get() = y + height
}

internal class Period(
  val columnNumber: Int,
  val startAtSec: Long,
  val endAtSec: Long,
  val positionInItemProvider: Int,
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem

internal class ColumnHeader(
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem

internal data class TimeLabel(
  override val width: Int,
  override val height: Int,
  override val x: Int,
  override val y: Int,
  override val content: @Composable () -> Unit,
) : VirtualMeasuredItem
