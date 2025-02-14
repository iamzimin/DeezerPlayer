package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.resource.R
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.presentation.mapper.toFormattedTime
import com.evg.track_playback.presentation.mapper.toFormattedTimeWithProgress
import com.evg.track_playback.presentation.model.PlaylistState
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.ui.extensions.clickableRipple
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.VerticalPadding

@Composable
fun SongScreen(
    dispatch: (TrackPlaybackAction) -> Unit,
    state: PlaylistState.Ready,
    isPlaying: Boolean,
    progress: Float,
    duration: Long,
    isTrackDownloading: Boolean,
    onPreviousScreen: () -> Unit,
) {
    val currentPlayId = state.currentPlayingIndex?.coerceAtMost(state.trackLists.lastIndex)
    if (currentPlayId == null) {
        onPreviousScreen()
        return
    }

    val currentTrack = state.trackLists[currentPlayId]
    var playingSongIndex by remember { mutableIntStateOf(0) }
    var seekbarPosition by remember { mutableFloatStateOf(progress) }

    val pagerState = rememberPagerState(
        initialPage = currentPlayId,
        pageCount = { state.trackLists.count() },
    )

    LaunchedEffect(currentPlayId) {
        playingSongIndex = currentPlayId
        pagerState.animateScrollToPage(
            playingSongIndex,
            animationSpec = tween(500)
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        playingSongIndex = pagerState.currentPage
        dispatch(TrackPlaybackAction.PlayByIndex(pagerState.currentPage))
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        val configuration = LocalConfiguration.current

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {

            AnimatedContent(
                targetState = playingSongIndex,
                transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
                label = "",
            ) { animatedIndex ->
                val index = animatedIndex.coerceAtMost(state.trackLists.lastIndex)
                Text(
                    text = state.trackLists[index].trackTitle,
                    color = AppTheme.colors.text,
                    style = AppTheme.typography.heading,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedContent(
                targetState = playingSongIndex,
                transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
                label = "",
            ) { animatedIndex ->
                val index = animatedIndex.coerceAtMost(state.trackLists.lastIndex)
                Text(
                    text = state.trackLists[index].trackTitle,
                    color = AppTheme.colors.text,
                    style = AppTheme.typography.body,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(modifier = Modifier.height(VerticalPadding))

            val pageWidth = (configuration.screenWidthDp / 1.7).dp
            val horizontalPadding = ((configuration.screenWidthDp.dp - pageWidth) / 2)
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                pageSize = PageSize.Fixed(pageWidth),
                verticalAlignment = Alignment.CenterVertically,
            ) { page ->
                if (page == pagerState.currentPage) {
                    VinylAlbumCoverAnimation(isSongPlaying = isPlaying, albumCover = currentTrack.albumCover)
                } else {
                    VinylAlbumCoverAnimation(isSongPlaying = false, albumCover = currentTrack.albumCover)
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
            ) {

                Slider(
                    value = progress,
                    onValueChange = { newValue ->
                        seekbarPosition = newValue
                    },
                    onValueChangeFinished = {
                        dispatch(TrackPlaybackAction.SeekTo(seekbarPosition))
                    },
                    valueRange = 0f..100f,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = duration.toFormattedTimeWithProgress(progress = progress),
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        color = AppTheme.colors.text,
                        style = AppTheme.typography.small,
                    )

                    Text(
                        text = duration.toFormattedTime(),
                        modifier = Modifier
                            .padding(10.dp),
                        color = AppTheme.colors.text,
                        style = AppTheme.typography.small,
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
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
                    painter = if (isPlaying) {
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

                if (isTrackDownloading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        modifier = Modifier
                            .size(iconSize)
                            .clickableRipple {
                                if (currentTrack.isDownloaded) {
                                    dispatch(TrackPlaybackAction.RemoveTrack)
                                } else {
                                    dispatch(TrackPlaybackAction.SaveTrack)
                                }
                            },
                        painter = if (currentTrack.isDownloaded) {
                            painterResource(R.drawable.trash)
                        } else {
                            painterResource(R.drawable.download)
                        },
                        contentDescription = null,
                        tint = AppTheme.colors.text,
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SongScreenPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            SongScreen(
                dispatch = {},
                state = PlaylistState.Ready(
                    trackLists = listOf(
                        TrackData(
                            trackID = 1,
                            trackTitle = "Bad Dreams",
                            trackPreview = "https://cdnt-preview.dzcdn.net/api/1/1/b/4/7/0/b4764070eb914f3885a3bd9bdd497934.mp3",
                            artistName = "Teddy Swims",
                            albumID = 2,
                            albumCover = "https://cdn-images.dzcdn.net/images/cover/ebb148dd7d9d124ea9fbe39d4576fa46/250x250-000000-80-0-0.jpg",
                            isDownloaded = true,
                        )
                    ),
                    currentPlayingIndex = 0,
                ),
                isPlaying = false,
                progress = 50f,
                duration = 200,
                isTrackDownloading = false,
                onPreviousScreen = {},
            )
        }
    }
}