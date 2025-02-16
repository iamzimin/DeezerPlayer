package com.evg.track_playback.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.evg.ui.Shimmer
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.BorderRadius
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.HorizontalPadding
import kotlin.random.Random

/**
 * Эффект загрузки для экрана воспроизведения трека
 */
@Composable
fun SongScreenShimmer() {
    val iconSize = 40.dp
    val innerMargin = 30.dp
    val configuration = LocalConfiguration.current
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 3 },
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(10.dp))

        Shimmer(
            modifier = Modifier
                .height(15.dp)
                .width(Random.nextInt(70, 251).dp)
                .clip(shape = RoundedCornerShape(BorderRadius))
        )
        Spacer(Modifier.height(20.dp))

        val pageWidth = (configuration.smallestScreenWidthDp / 1.4).dp
        val horizontalPadding = ((configuration.smallestScreenWidthDp.dp - pageWidth) / 2)
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSize = PageSize.Fixed(pageWidth),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Shimmer(
                modifier = Modifier
                    .size(pageWidth)
                    .padding(20.dp)
                    .width(Random.nextInt(70, 151).dp)
                    .clip(shape = RoundedCornerShape(BorderRadius))
            )
        }

        Spacer(Modifier.height(50.dp))

        Shimmer(
            modifier = Modifier
                .height(20.dp)
                .width(Random.nextInt(70, 251).dp)
                .clip(shape = RoundedCornerShape(BorderRadius))
        )
        Spacer(Modifier.height(10.dp))
        Shimmer(
            modifier = Modifier
                .height(15.dp)
                .width(Random.nextInt(70, 251).dp)
                .clip(shape = RoundedCornerShape(BorderRadius))
        )

        Spacer(Modifier.height(50.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = HorizontalPadding + 10.dp)
        ) {
            Shimmer(
                modifier = Modifier
                    .height(10.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(BorderRadius))
            )

            Spacer(Modifier.height(20.dp))

            Row {
                Shimmer(
                    modifier = Modifier
                        .height(10.dp)
                        .width(40.dp)
                        .clip(shape = RoundedCornerShape(BorderRadius))
                )
                Spacer(Modifier.weight(1f))
                Shimmer(
                    modifier = Modifier
                        .height(10.dp)
                        .width(40.dp)
                        .clip(shape = RoundedCornerShape(BorderRadius))
                )
            }
        }

        Spacer(Modifier.height(35.dp))


        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                val (btnPrev, btnPause, btnNext, btnSave) = createRefs()

                Shimmer(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(shape = RoundedCornerShape(BorderRadius)),
                    modifierTop = Modifier
                        .constrainAs(btnPrev) {
                            end.linkTo(btnPause.start, margin = innerMargin)
                            centerVerticallyTo(parent)
                        },
                )

                Shimmer(
                    modifier = Modifier
                        .size(iconSize + 10.dp)
                        .clip(shape = RoundedCornerShape(BorderRadius)),
                    modifierTop = Modifier
                        .constrainAs(btnPause) {
                            centerHorizontallyTo(parent)
                            centerVerticallyTo(parent)
                        },
                )

                Shimmer(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(shape = RoundedCornerShape(BorderRadius)),
                    modifierTop = Modifier
                        .constrainAs(btnNext) {
                            start.linkTo(btnPause.end, margin = innerMargin)
                            centerVerticallyTo(parent)
                        },
                )

                Shimmer(
                    modifier = Modifier
                        .size(iconSize)
                        .clip(shape = RoundedCornerShape(BorderRadius)),
                    modifierTop = Modifier
                        .constrainAs(btnSave) {
                            end.linkTo(parent.end, margin = HorizontalPadding)
                            centerVerticallyTo(parent)
                        },
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SongScreenShimmerPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            SongScreenShimmer()
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun SongScreenShimmerPreview2(darkTheme: Boolean = true) {
    SongScreenPreview(darkTheme = darkTheme)
}