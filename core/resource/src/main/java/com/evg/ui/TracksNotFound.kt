package com.evg.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.evg.resource.R
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme
import com.evg.ui.theme.VerticalPadding

@Composable
fun TracksNotFound(
    isSwipeAvailable: Boolean = true,
) {
    val displayText = buildString {
        append(stringResource(id = R.string.list_tracks_is_empty))
        append("\n")
        if (isSwipeAvailable) {
            append(stringResource(id = R.string.swipe_to_update))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .size(70.dp),
                painter = painterResource(id = R.drawable.sad),
                contentDescription = null,
                tint = AppTheme.colors.text,
            )

            Spacer(modifier = Modifier.height(VerticalPadding))

            Text(
                text = displayText,
                style = AppTheme.typography.body,
                color = AppTheme.colors.text,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun TracksNotFoundPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            TracksNotFound()
        }
    }
}