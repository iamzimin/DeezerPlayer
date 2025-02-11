package com.evg.chart.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.usecase.ChartUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val chartUseCases: ChartUseCases,
): ContainerHost<ChartState, ChartSideEffect>, ViewModel() {
    override val container = container<ChartState, ChartSideEffect>(ChartState())

    init {
        getChart()
    }

    fun dispatch(action: ChartAction) {
        when (action) {
            is ChartAction.GetChart -> getChart()
            is ChartAction.SearchTrack -> searchTrack(query = action.query)
        }
    }

    private fun getChart() = intent {
        viewModelScope.launch {
            reduce { state.copy(isChartLoading = true) }
            when (val response = chartUseCases.getChartUseCase.invoke()) {
                is ServerResult.Success -> {
                    reduce { state.copy(chartTracks = response.data) }
                }
                is ServerResult.Error -> {
                    postSideEffect(ChartSideEffect.ChartLoadFail(error = response.error))
                }
            }
            reduce { state.copy(isChartLoading = false) }
        }
    }

    private fun searchTrack(query: String) = intent {
        viewModelScope.launch {
            reduce { state.copy(isChartLoading = true) }
            chartUseCases.searchTrackUseCase.invoke(query = query)
                .cachedIn(viewModelScope)
                .collect { tracks ->
                    container.stateFlow.value.foundedTracks.value = tracks
                    reduce { state.copy(isChartLoading = false) }
                }
        }
    }
}
