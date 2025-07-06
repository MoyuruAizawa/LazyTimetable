package io.moyuru.lazytimetable.timecolumn

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import io.moyuru.lazytimetable.LazyTimetableScopeImpl

@OptIn(ExperimentalFoundationApi::class)
internal class LazyTimeColumnItemProvider(
  private val scope: LazyTimetableScopeImpl,
) : LazyLayoutItemProvider {
  override val itemCount: Int
    get() = scope.timeLabels.size

  @Composable
  override fun Item(index: Int, key: Any) {
    scope.timeLabels[index].content()
  }
}
