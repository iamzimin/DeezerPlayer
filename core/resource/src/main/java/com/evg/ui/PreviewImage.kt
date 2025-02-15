package com.evg.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.evg.resource.R
import com.evg.ui.theme.BorderRadius


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
            Shimmer(
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        error = {
            ImageError()
        },
    )
}

@Composable
private fun ImageError() {
    val backgroundColor = if (isSystemInDarkTheme()) Color.Gray else Color.LightGray
    val iconColor = if (!isSystemInDarkTheme()) Color.Gray else Color.LightGray
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape = RoundedCornerShape(BorderRadius))
            .background(backgroundColor),
    ) {
        Icon(
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.file_warning),
            contentDescription = null,
            tint = iconColor,
        )
    }
}
