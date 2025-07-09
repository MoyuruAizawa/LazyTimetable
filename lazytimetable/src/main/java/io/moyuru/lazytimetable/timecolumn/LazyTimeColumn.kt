package io.moyuru.lazytimetable.timecolumn

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import io.moyuru.lazytimetable.LazyTimetableScopeImpl
import io.moyuru.lazytimetable.LazyTimetableState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LazyTimeColumn(
  paddingLeft: Dp,
  state: LazyTimetableState,
  scope: LazyTimetableScopeImpl,
  modifier: Modifier = Modifier,
) {
  LazyLayout(
    itemProvider = { LazyTimeColumnItemProvider(scope) },
    modifier = modifier.clipToBounds(),
    measurePolicy = lazyTimeColumnMeasurementPolicy(paddingLeft, state, scope),
  )
}
