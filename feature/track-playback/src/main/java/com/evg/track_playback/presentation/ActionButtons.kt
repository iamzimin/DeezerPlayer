package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.evg.resource.R
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.ui.extensions.clickableRipple
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding


@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isTrackDownloading: Boolean,
    isDownloaded: Boolean,
    dispatch: (TrackPlaybackAction) -> Unit,
) {
    val iconSize = 35.dp
    val innerMargin = 30.dp

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        val (btnPrev, btnPause, btnNext, btnSave) = createRefs()

        Icon(
            painter = painterResource(R.drawable.backward),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(iconSize)
                .constrainAs(btnPrev) {
                    end.linkTo(btnPause.start, margin = innerMargin)
                    centerVerticallyTo(parent)
                }
                .clickableRipple {
                    dispatch(TrackPlaybackAction.SeekToPrev)
                }
        )

        Icon(
            painter = if (isPlaying) {
                painterResource(R.drawable.pause)
            } else {
                painterResource(R.drawable.play)
            },
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(iconSize + 10.dp)
                .constrainAs(btnPause) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
                .clickableRipple {
                    dispatch(TrackPlaybackAction.PlayPause)
                }
        )

        Icon(
            painter = painterResource(R.drawable.backward),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(iconSize)
                .rotate(180f)
                .constrainAs(btnNext) {
                    start.linkTo(btnPause.end, margin = innerMargin)
                    centerVerticallyTo(parent)
                }
                .clickableRipple {
                    dispatch(TrackPlaybackAction.SeekToNext)
                }
        )

        if (isTrackDownloading) {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(btnSave) {
                    end.linkTo(parent.end, margin = HorizontalPadding)
                    centerVerticallyTo(parent)
                },
                color = AppTheme.colors.primary,
            )
        } else {
            Icon(
                painter = if (isDownloaded) {
                    painterResource(R.drawable.trash)
                } else {
                    painterResource(R.drawable.download)
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
                    .constrainAs(btnSave) {
                        end.linkTo(parent.end, margin = HorizontalPadding)
                        centerVerticallyTo(parent)
                    }
                    .clickableRipple {
                        if (isDownloaded) {
                            dispatch(TrackPlaybackAction.RemoveTrack)
                        } else {
                            dispatch(TrackPlaybackAction.SaveTrack)
                        }
                    }
            )
        }
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun ActionButtonsPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            ActionButtons(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = true,
                isDownloaded = false,
                isTrackDownloading = false,
                dispatch = {},
            )
        }
    }
}