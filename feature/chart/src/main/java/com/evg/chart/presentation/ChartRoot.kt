package com.evg.chart.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.evg.resource.R
import com.evg.chart.presentation.mvi.ChartAction
import com.evg.chart.presentation.mvi.ChartSideEffect
import com.evg.chart.presentation.mvi.ChartViewModel
import com.evg.ui.mapper.toErrorMessage
import com.evg.ui.snackbar.SnackBarAction
import com.evg.ui.snackbar.SnackBarController
import com.evg.ui.snackbar.SnackBarEvent
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * Корневой экран чарта с отображением списка треков
 *
 * @param viewModel ViewModel для управления состоянием чарта
 * @param modifier Модификатор для стилизации компонента
 * @param onPlayerScreen Колбэк, вызываемый при выборе трека для перехода на экран плеера по id
 */
@Composable
fun ChartRoot(
    viewModel: ChartViewModel = hiltViewModel<ChartViewModel>(),
    modifier: Modifier,
    onPlayerScreen: (id: Long) -> Unit,
) {
    val context = LocalContext.current
    val updateString = stringResource(R.string.update)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ChartSideEffect.ChartLoadFail -> {
                SnackBarController.sendEvent(SnackBarEvent(
                    message = sideEffect.error.toErrorMessage(context),
                    action = SnackBarAction(
                        name = updateString,
                        action = { viewModel.dispatch(ChartAction.GetChart) },
                    ),
                ))
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