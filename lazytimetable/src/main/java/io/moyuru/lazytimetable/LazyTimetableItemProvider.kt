package io.moyuru.lazytimetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable

@OptIn(ExperimentalFoundationApi::class)
internal class LazyTimetableItemProvider(
  private val scope: LazyTimetableScopeImpl,
) : LazyLayoutItemProvider {
  override val itemCount: Int
    get() = scope.items.size

  @Composable
  override fun Item(index: Int, key: Any) {
    scope.items[index].content()
  }
}
