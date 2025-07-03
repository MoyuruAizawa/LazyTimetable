package io.moyuru.lazytimetablesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.moyuru.lazytimetable.LazyTimetable
import io.moyuru.lazytimetablesample.ui.theme.LazyTimetableSampleTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LazyTimetableSampleTheme {
        Scaffold {
          LazyTimetable(
            columnWidth = 120.dp,
            heightPerMinute = 1.5.dp,
            columnHeaderHeight = 80.dp + it.calculateTopPadding(),
            timeColumnWidth = 100.dp,
            baseEpochSec = tomorrowland.startAt,
            timeLabel = {
              Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
              ) {
                val label = remember(it) {
                  Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("HH:mm"))
                }
                Text(text = label)
              }
            },
            modifier = Modifier
              .fillMaxSize()
          ) {
            tomorrowland.stages.forEach { stage ->
              column(
                header = {
                  Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                      .fillMaxSize()
                      .background(Color.White)
                      .padding(top = it.calculateTopPadding())
                  ) {
                    Text(
                      text = stage.title,
                      fontSize = 14.sp,
                    )
                  }
                }
              ) {
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
