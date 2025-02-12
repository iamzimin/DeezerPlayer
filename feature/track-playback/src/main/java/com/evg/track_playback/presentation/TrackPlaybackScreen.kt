package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.track_playback.presentation.mvi.TrackPlaybackState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.BorderRadius
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import com.evg.ui.theme.VerticalPadding
import com.valentinilk.shimmer.shimmer

@Composable
fun TrackPlaybackScreen(
    state: TrackPlaybackState,
    dispatch: (TrackPlaybackAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPosition by remember { mutableFloatStateOf(state.progress) }

    Column(
        modifier = modifier
            .padding(
                horizontal = HorizontalPadding,
                vertical = VerticalPadding,
            )
    ) {
        if (state.isPlaylistLoading) {
            CircularProgressIndicator()
        }

        Text(
            text = state.currentSelectedTrack.trackTitle,
            color = AppTheme.colors.text,
            style = AppTheme.typography.body,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = state.currentSelectedTrack.artistName,
            color = AppTheme.colors.text,
            style = AppTheme.typography.body,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        SubcomposeAsyncImage( //TODO вынести
            model = state.currentSelectedTrack.albumCover,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(BorderRadius)),
            contentDescription = state.currentSelectedTrack.albumCover,
            alignment = Alignment.CenterStart,
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(BorderRadius))
                        .shimmer(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
            },
            error = {

            },
        )

        Slider(
            value = state.progress,
            onValueChange = { newValue ->
                currentPosition = newValue
            },
            onValueChangeFinished = {
                dispatch(TrackPlaybackAction.SeekTo(currentPosition))
            },
            valueRange = 0f..100f,
        )

        Row {
            Button(
                onClick = {
                    dispatch(TrackPlaybackAction.SeekToPrev)
                }
            ) { Text("Backward") }
            Button(
                onClick = {
                    dispatch(TrackPlaybackAction.PlayPause)
                }
            ) { Text(if (state.isPlaying) "Pause" else "Play") }
            Button(
                onClick = {
                    dispatch(TrackPlaybackAction.SeekToNext)
                }
            ) { Text("Forward") }
        }

    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TrackPlaybackScreenPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            TrackPlaybackScreen(
                state = TrackPlaybackState(
                    isPlaylistLoading = false,
                    currentSelectedTrack = TrackData(
                        trackID = 1,
                        trackTitle = "Bad Dreams",
                        trackPreview = "https://cdnt-preview.dzcdn.net/api/1/1/b/4/7/0/b4764070eb914f3885a3bd9bdd497934.mp3",
                        artistName = "Teddy Swims",
                        albumID = 2,
                        albumCover = "https://cdn-images.dzcdn.net/images/cover/ebb148dd7d9d124ea9fbe39d4576fa46/250x250-000000-80-0-0.jpg"
                    )
                ),
                dispatch = {},
            )
        }
    }
}