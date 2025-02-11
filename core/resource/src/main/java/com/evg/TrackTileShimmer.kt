package com.evg

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.BorderRadius
import com.evg.ui.theme.DeezerPlayerTheme
import com.valentinilk.shimmer.shimmer
import kotlin.random.Random

@Composable
fun TrackTileShimmer() {
    val height = 70.dp

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(BorderRadius))
    ) {
        Row {
            Box(
                modifier = Modifier.shimmer(),
            ) {
                Box(
                    modifier = Modifier
                        .size(height)
                        .clip(shape = RoundedCornerShape(BorderRadius))
                        .background(AppTheme.colors.shimmer)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    ),
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .height(10.dp)
                        .width(Random.nextInt(70, 151).dp)
                        .clip(shape = RoundedCornerShape(BorderRadius))
                        .background(AppTheme.colors.shimmer)
                )
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .height(10.dp)
                        .width(Random.nextInt(70, 151).dp)
                        .clip(shape = RoundedCornerShape(BorderRadius))
                        .background(AppTheme.colors.shimmer)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun LazyColumnShimmerPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            Column {
                TrackTileShimmer()
                HorizontalDivider()
                TrackTilePreview()
            }
        }
    }
}