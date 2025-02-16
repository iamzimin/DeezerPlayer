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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.track_playback.domain.model.TrackData
import com.evg.track_playback.presentation.model.PlaylistState
import com.evg.track_playback.presentation.mvi.TrackPlaybackAction
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding

/**
 * Экран воспроизведения трека
 *
 * @param dispatch Функция для отправки действий воспроизведения
 * @param state Текущее состояние плейлиста
 * @param isPlaying Флаг, указывающий, играет ли трек
 * @param progress Текущий прогресс воспроизведения
 * @param duration Длительность трека
 * @param isTrackDownloading Флаг загрузки трека
 * @param onPreviousScreen Функция для возврата на предыдущий экран
 * @param onBackgroundImageReady Функция отправки в root url изображения
 */
@Composable
fun SongScreen(
    dispatch: (TrackPlaybackAction) -> Unit,
    state: PlaylistState.Ready,
    isPlaying: Boolean,
    progress: Float,
    duration: Long,
    isTrackDownloading: Boolean,
    onPreviousScreen: () -> Unit,
    onBackgroundImageReady: (url: String) -> Unit,
) {
    val currentPlayId = state.currentPlayingIndex?.coerceAtMost(state.trackLists.lastIndex)
    if (currentPlayId == null || state.trackLists.isEmpty()) {
        onPreviousScreen()
        return
    }

    val configuration = LocalConfiguration.current

    val currentTrack = state.trackLists[currentPlayId]
    var playingSongIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = currentPlayId,
        pageCount = { state.trackLists.count() },
    )

    LaunchedEffect(currentTrack) {
        onBackgroundImageReady(currentTrack.albumCover)
    }

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


    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedContent(
            targetState = playingSongIndex,
            transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
        ) { animatedIndex ->
            val index = animatedIndex.coerceAtMost(state.trackLists.lastIndex)
            Text(
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                text = state.trackLists[index].albumTitle,
                color = Color.White,
                style = AppTheme.typography.body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }


        Spacer(Modifier.height(20.dp))

        val pageWidth = (configuration.smallestScreenWidthDp / 1.4).dp
        val horizontalPadding = ((configuration.smallestScreenWidthDp.dp - pageWidth) / 2)
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSize = PageSize.Fixed(pageWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) { page ->
            val isCurrentPage = page == pagerState.currentPage
            AlbumCoverAnimation(
                modifier = Modifier
                    .size(pageWidth)
                    .padding(horizontal = 20.dp),
                isSongPlaying = isCurrentPage && isPlaying,
                albumCover = state.trackLists[page].albumCover
            )
        }

        Spacer(Modifier.height(40.dp))


        AnimatedContent(
            targetState = playingSongIndex,
            transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
        ) { animatedIndex ->
            val index = animatedIndex.coerceAtMost(state.trackLists.lastIndex)
            Text(
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                text = state.trackLists[index].trackTitle,
                color = Color.White,
                style = AppTheme.typography.heading,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(5.dp))
        AnimatedContent(
            targetState = playingSongIndex,
            transitionSpec = { (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut()) },
        ) { animatedIndex ->
            val index = animatedIndex.coerceAtMost(state.trackLists.lastIndex)
            Text(
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                text = state.trackLists[index].artistName,
                color = Color.White,
                style = AppTheme.typography.body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(30.dp))


        Slider(
            progress = progress,
            duration = duration,
            seekTo = { dispatch(TrackPlaybackAction.SeekTo(it)) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ActionButtons(
            modifier = Modifier.fillMaxWidth(),
            isPlaying = isPlaying,
            isDownloaded = currentTrack.isDownloaded,
            isTrackDownloading = isTrackDownloading,
            dispatch = dispatch,
        )
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
                            albumTitle = "Album Title",
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
                onBackgroundImageReady = {},
            )
        }
    }
}