package io.moyuru.lazytimetablesample

import android.os.Bundle
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
        Scaffold { contentPaddings ->
          LazyTimetable(
            contentPadding = contentPaddings,
            horizontalSpacing = 4.dp,
            columnWidth = 120.dp,
            heightPerMinute = 1.5.dp,
            columnHeaderHeight = 80.dp,
            columnHeaderColor = Color.White,
            timeColumnWidth = 100.dp,
            timeColumnColor = Color.White,
            baseEpochSec = tomorrowland.startAt,
            timeLabel = { TimeLabel(it) },
            modifier = Modifier.fillMaxSize(),
          ) {
            tomorrowland.stages.forEach { stage ->
              column(header = { Header(stage) }) {
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
      .padding(vertical = 2.dp)
      .background(Color.DarkGray)
      .padding(4.dp),
  ) {
    Text(text = title, color = Color.White, modifier = Modifier)
  }
}

@Composable
fun Header(stage: Stage, modifier: Modifier = Modifier) {
  Box(contentAlignment = Alignment.Center, modifier = modifier) {
    Text(
      text = stage.title,
      fontSize = 14.sp,
    )
  }
}

@Composable
fun TimeLabel(epochSec: Long, modifier: Modifier = Modifier) {
  Box(contentAlignment = Alignment.TopCenter, modifier = modifier) {
    val label = remember(epochSec) {
      Instant.ofEpochSecond(epochSec)
        .atZone(ZoneId.of("Europe/Brussels"))
        .format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    Text(text = label)
  }
}
