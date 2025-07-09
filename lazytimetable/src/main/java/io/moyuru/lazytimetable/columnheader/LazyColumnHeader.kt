package io.moyuru.lazytimetable.columnheader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import io.moyuru.lazytimetable.LazyTimetableScopeImpl
import io.moyuru.lazytimetable.LazyTimetableState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyColumnHeader(
  timeColumnWidth: Dp,
  contentPadding: PaddingValues,
  state: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
  modifier: Modifier = Modifier,
) {
  LazyLayout(
    itemProvider = { LazyColumnHeaderItemProvider(scope) },
    modifier = modifier.clipToBounds(),
    measurePolicy = lazyColumnHeaderMeasurementPolicy(timeColumnWidth, contentPadding, state, scope),
  )
}
