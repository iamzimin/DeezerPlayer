package com.evg.chart.presentation

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.evg.chart.presentation.mvi.ChartAction
import com.evg.chart.presentation.mvi.ChartState
import com.evg.ui.theme.AppTheme
import com.evg.ui.theme.DeezerPlayerTheme

@Composable
fun ChartScreen(
    state: ChartState,
    dispatch: (action: ChartAction) -> Unit,
    modifier: Modifier = Modifier,
    onPlayerScreen: (id: Int) -> Unit,
) {

}

@Composable
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
fun StatisticsScreenPreview(darkTheme: Boolean = true) {
    DeezerPlayerTheme(darkTheme = darkTheme) {
        Surface(color = AppTheme.colors.background) {
            ChartScreen(
                state = ChartState(
                    isChartLoading = false,
                ),
                dispatch = {},
                onPlayerScreen = {},
            )
        }
    }
}