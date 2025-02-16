package com.evg.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.ui.extensions.clickableRipple
import com.evg.ui.extensions.makeTransparent
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.BorderRadius
import com.evg.ui.theme.DeezerPlayerTheme

/**
 * Карточка трека с изображением альбома, названием и именем исполнителя
 *
 * @param albumCover Ссылка на обложку альбома
 * @param trackTitle Название трека
 * @param artistName Имя исполнителя
 * @param onClick Колбэк, вызываемый при нажатии на карточку
 */
@Composable
fun TrackTile(
    albumCover: String,
    trackTitle: String,
    artistName: String,
    onClick: () -> Unit,
) {
    val height = 70.dp

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(BorderRadius))
            .clickableRipple {
                onClick()
            }
    ) {
        Row {
            PreviewImage(
                modifier = Modifier.size(height),
                albumCover = albumCover,
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(
                        horizontal = 10.dp,
                        vertical = 10.dp
                    ),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = trackTitle,
                    color = AppTheme.colors.text,
                    style = AppTheme.typography.body.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = artistName,
                    color = AppTheme.colors.text.makeTransparent(0.2f),
                    style = AppTheme.typography.body,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }


        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TrackTilePreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            TrackTile(
                albumCover = "",
                trackTitle = "Title",
                artistName = "artist name",
                onClick = {},
            )
        }
    }
}