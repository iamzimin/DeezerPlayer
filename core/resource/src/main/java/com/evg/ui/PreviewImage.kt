package com.evg.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.SubcomposeAsyncImage
import com.evg.ui.theme.BorderRadius
import com.valentinilk.shimmer.shimmer


@Composable
fun PreviewImage(
    modifier: Modifier,
    albumCover: String,
) {
    SubcomposeAsyncImage(
        model = albumCover,
        modifier = modifier
            .clip(RoundedCornerShape(BorderRadius)),
        contentDescription = albumCover,
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
            ImageError()
        },
    )
}

@Composable
private fun ImageError() {
    /*Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(BorderRadius))
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.onSurface,
                RoundedCornerShape(BorderRadius)
            )
    ) {
        Icon(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.),
            contentDescription = "Error",
            tint =Color.Red,
        )
    }*/
}
