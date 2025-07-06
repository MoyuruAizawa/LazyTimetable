package io.moyuru.lazytimetable

import androidx.compose.runtime.Composable

internal interface VirtualMeasuredItem {
  val positionInItemProvider: Int
  val width: Int
  val height: Int
  val x: Int
  val y: Int
  val content: @Composable () -> Unit
  val contentType: ContentType

  val left get() = x
  val top get() = y
  val right get() = x + width
  val bottom get() = y + height
}

internal enum class ContentType {
  Period
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
) : VirtualMeasuredItem {
  override val contentType = ContentType.Period
}

internal class ColumnHeader(
  val width: Int,
  val height: Int,
  val x: Int,
  val y: Int,
  val content: @Composable () -> Unit,
)

internal data class TimeLabel(
  val positionInItemProvider: Int,
  val width: Int,
  val height: Int,
  val x: Int,
  val y: Int,
  val content: @Composable () -> Unit,
)
