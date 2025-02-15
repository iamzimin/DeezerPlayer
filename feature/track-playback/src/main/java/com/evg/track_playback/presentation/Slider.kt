package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.track_playback.presentation.mapper.toFormattedTime
import com.evg.track_playback.presentation.mapper.toFormattedTimeWithProgress
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Slider(
    progress: Float,
    duration: Long,
    seekTo: (Float) -> Unit,
) {
    var seekbarPosition by remember { mutableFloatStateOf(progress) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = HorizontalPadding),
    ) {

        androidx.compose.material3.Slider(
            value = progress,
            onValueChange = { newValue ->
                seekbarPosition = newValue
            },
            onValueChangeFinished = {
                seekTo(seekbarPosition)
            },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = AppTheme.colors.primary,
                activeTrackColor = AppTheme.colors.primary,
                inactiveTrackColor = AppTheme.colors.primary,
            ),
            track = { sliderState ->
                val fraction by remember {
                    derivedStateOf {
                        (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                    }
                }

                Box(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .fillMaxWidth(fraction)
                            .align(Alignment.CenterStart)
                            .height(6.dp)
                            .padding(end = 16.dp)
                            .background(AppTheme.colors.secondary, CircleShape)
                    )
                    Box(
                        Modifier
                            .fillMaxWidth(1f - fraction)
                            .align(Alignment.CenterEnd)
                            .height(2.dp)
                            .padding(start = 16.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            },
            thumb = {
                Box(
                    Modifier
                        .size(25.dp)
                        .padding(2.dp)
                        .background(AppTheme.colors.primary, CircleShape)
                        .shadow(10.dp, CircleShape)
                )
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = duration.toFormattedTimeWithProgress(progress = progress),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 13.dp),
                color = Color.White,
                style = AppTheme.typography.small,
            )

            Text(
                text = duration.toFormattedTime(),
                modifier = Modifier
                    .padding(5.dp),
                color = Color.White,
                style = AppTheme.typography.small,
            )
        }
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SliderPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            Slider(
                progress = 40f,
                duration = 30000,
                seekTo = {},
            )
        }
    }
}