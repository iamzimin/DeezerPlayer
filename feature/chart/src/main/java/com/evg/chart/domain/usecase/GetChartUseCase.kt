package com.evg.chart.domain.usecase

import com.evg.chart.domain.repository.ChartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChartUseCase @Inject constructor(
    private val chartRepository: ChartRepository,
) {
    /*suspend fun invoke(filter: ChartFilter): Flow<> {
        TODO()
    }*/
}