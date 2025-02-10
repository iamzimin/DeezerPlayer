package com.evg.chart.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.evg.chart.presentation.mvi.ChartViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ChartRoot(
    viewModel: ChartViewModel = hiltViewModel<ChartViewModel>(),
    modifier: Modifier,
    onPlayerScreen: (id: Int) -> Unit,
) {

    ChartScreen(
        state = viewModel.collectAsState().value,
        dispatch = viewModel::dispatch,
        modifier = modifier,
        onPlayerScreen = onPlayerScreen,
    )
}