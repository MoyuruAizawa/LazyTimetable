package io.moyuru.lazytimetablesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.moyuru.lazytimetable.LazyTimetable
import io.moyuru.lazytimetable.columns
import io.moyuru.lazytimetable.items
import io.moyuru.lazytimetablesample.ui.theme.Colors
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
          Timetable(contentPaddings)
        }
      }
    }
  }
}

@Composable
fun Timetable(contentPaddings: PaddingValues) {
  LazyTimetable(
    contentPadding = contentPaddings,
    horizontalSpacing = 4.dp,
    columnWidth = 120.dp,
    heightPerMinute = 1.5.dp,
    columnHeaderHeight = 80.dp,
    columnHeaderColor = Brush.horizontalGradient(listOf(Colors.TML_PRIMARY, Colors.TML_ACCENT)),
    timeColumnWidth = 100.dp,
    timeColumnColor = SolidColor(Color.Black),
    baseEpochSec = tomorrowland.startAt,
    timeLabel = { TimeLabel(it) },
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black),
  ) {
    columns(
      tomorrowland.stages,
      header = { Header(it) },
    ) { stage ->
      items(
        stage.periods,
        durationSec = { period -> period.durationSec },
      ) { period ->
        when (period) {
          is Period.Empty -> Spacer(Modifier)
          is Period.Show -> Show(period.title)
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
      .padding(bottom = 4.dp)
      .background(Color.White)
      .padding(8.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.bodyMedium,
      color = Color.Black,
    )
  }
}

@Composable
fun Header(stage: Stage, modifier: Modifier = Modifier) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = modifier.padding(8.dp),
  ) {
    Text(
      text = stage.title,
      style = MaterialTheme.typography.titleSmall,
      color = Color.White,
    )
  }
}

@Composable
fun TimeLabel(epochSec: Long, modifier: Modifier = Modifier) {
  Box(contentAlignment = Alignment.TopCenter, modifier = modifier) {
    val label = remember(epochSec) {
      Instant
        .ofEpochSecond(epochSec)
        .atZone(ZoneId.of("Europe/Brussels"))
        .format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    Text(
      text = label,
      style = MaterialTheme.typography.labelMedium,
      color = Color(0xFFA0AEC0),
    )
  }
}
