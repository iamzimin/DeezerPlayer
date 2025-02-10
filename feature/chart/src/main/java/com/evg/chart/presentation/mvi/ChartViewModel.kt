package com.evg.chart.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        }
    }

    private fun getChart() = intent {
        reduce { state.copy(isChartLoading = true) }
        viewModelScope.launch {

        }
    }
}