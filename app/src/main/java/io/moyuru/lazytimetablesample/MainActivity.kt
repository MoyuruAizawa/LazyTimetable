package io.moyuru.lazytimetablesample

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.moyuru.lazytimetable.LazyTimetable
import io.moyuru.lazytimetable.LazyTimetableState
import io.moyuru.lazytimetablesample.ui.theme.LazyTimetableSampleTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LazyTimetableSampleTheme {
        LazyTimetable(
          columnWidth = 120.dp,
          heightPerMinute = 1.5.dp,
          modifier = Modifier.fillMaxSize(),
        ) {
          stages.forEach { stage ->
            column {
              stage.periods.forEach { period ->
                item(period.durationSec) {
                  when (period) {
                    is Period.Empty -> Spacer(Modifier)
                    is Period.Show -> Show(period.title)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun Show(title: String, modifier: Modifier = Modifier) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier
      .fillMaxSize()
      .padding(2.dp)
      .background(Color.DarkGray)
      .padding(4.dp),
  ) {
    Text(text = title, color = Color.White, modifier = Modifier)
  }
}
