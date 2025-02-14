package com.evg.chart.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.evg.chart.presentation.mvi.ChartSideEffect
import com.evg.chart.presentation.mvi.ChartViewModel
import com.evg.ui.mapper.toErrorMessage
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.snackbar.SnackBarEvent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChartRoot(
    viewModel: ChartViewModel = hiltViewModel<ChartViewModel>(),
    modifier: Modifier,
    onPlayerScreen: (id: Long) -> Unit,
) {
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ChartSideEffect.ChartLoadFail -> {
                SnackBarController.sendEvent(SnackBarEvent(sideEffect.error.toErrorMessage(context)))
            }
        }
    }

    ChartScreen(
        state = viewModel.collectAsState().value,
        dispatch = viewModel::dispatch,
        modifier = modifier,
        onPlayerScreen = onPlayerScreen,
    )
}