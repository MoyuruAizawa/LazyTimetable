package io.moyuru.lazytimetable.columnheader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import io.moyuru.lazytimetable.LazyTimetableScopeImpl

@OptIn(ExperimentalFoundationApi::class)
internal class LazyColumnHeaderItemProvider(
  private val scope: LazyTimetableScopeImpl,
) : LazyLayoutItemProvider {
  override val itemCount: Int
    get() = scope.columnHeaders.size

  @Composable
  override fun Item(index: Int, key: Any) {
    scope.columnHeaders[index].content()
  }
}
