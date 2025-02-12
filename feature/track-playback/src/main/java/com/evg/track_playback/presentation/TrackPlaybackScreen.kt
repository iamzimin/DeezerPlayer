package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.PreviewImage
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.track_playback.presentation.mvi.TrackPlaybackState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import com.evg.ui.theme.VerticalPadding
import com.evg.resource.R
import com.evg.ui.extensions.clickableRipple

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

        PreviewImage(
            albumCover = state.currentSelectedTrack.albumCover,
            size = 200.dp,
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            val iconSize = 40.dp
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .clickableRipple {
                        dispatch(TrackPlaybackAction.SeekToPrev)
                    },
                painter = painterResource(R.drawable.backward),
                contentDescription = null,
                tint = AppTheme.colors.text,
            )
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .clickableRipple {
                        dispatch(TrackPlaybackAction.PlayPause)
                    },
                painter = if (state.isPlaying) {
                    painterResource(R.drawable.pause)
                } else {
                    painterResource(R.drawable.play)
                },
                contentDescription = null,
                tint = AppTheme.colors.text,
            )
            Icon(
                modifier = Modifier
                    .size(iconSize)
                    .rotate(180f)
                    .clickableRipple {
                        dispatch(TrackPlaybackAction.SeekToNext)
                    },
                painter = painterResource(R.drawable.backward),
                contentDescription = null,
                tint = AppTheme.colors.text,
            )


            Spacer(Modifier.width(20.dp))

            if (state.isTrackDownloading) {
                CircularProgressIndicator()
            } else {
                Icon(
                    modifier = Modifier
                        .size(iconSize)
                        .clickableRipple {
                            dispatch(TrackPlaybackAction.SaveTrack)
                        },
                    painter = painterResource(R.drawable.download),
                    contentDescription = null,
                    tint = AppTheme.colors.text,
                )
            }
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