package io.moyuru.lazytimetable

import androidx.compose.ui.layout.Placeable

/**
 * Represents a visible item in the timetable layout.
 *
 * @param x The x-coordinate position for placement
 * @param y The y-coordinate position for placement
 * @param z The z-order (depth) for layering
 * @param columnNumber The column number this item belongs to, or null for non-column items
 * @param placeable The measured placeable for this item
 */
internal class VisibleItem(
  val x: Int,
  val y: Int,
  val z: Float,
  val columnNumber: Int?,
  val placeable: Placeable,
)
