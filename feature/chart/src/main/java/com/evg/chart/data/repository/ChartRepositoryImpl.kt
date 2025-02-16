package com.evg.chart.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.evg.api.data.SearchTrackPageSourceRemote
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.api.domain.utils.mapData
import com.evg.chart.domain.mapper.toTrackData
import com.evg.chart.domain.model.TrackData
import com.evg.chart.domain.repository.ChartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Реализация репозитория для работы с чартами
 *
 * @param apiRepository Репозиторий для взаимодействия с API Deezer
 * @param searchTrackPageSourceRemote Источник данных для пагинированного поиска треков
 */
class ChartRepositoryImpl(
    private val apiRepository: DeezerApiRepository,
    private val searchTrackPageSourceRemote: SearchTrackPageSourceRemote,
): ChartRepository {
    /**
     * Получает список треков чарта
     *
     * @return [ServerResult] со списком треков или ошибкой
     */
    override suspend fun getChartTracks(): ServerResult<List<TrackData>, NetworkError> {
        return apiRepository.getChart().mapData { chartResponse ->
            chartResponse.tracks.data.map { trackResponse ->
                trackResponse.toTrackData()
            }
        }
    }

    /**
     * Выполняет поиск треков по запросу
     *
     * @param query Строка запроса для поиска
     * @return [Flow] с пагинированными данными результатов поиска
     */
    override suspend fun searchTrack(query: String): Flow<PagingData<ServerResult<TrackData, NetworkError>>> {
        return Pager(
            PagingConfig(
                pageSize = 25,
            )
        ) { searchTrackPageSourceRemote.apply { this.query = query } }
            .flow
            .map { pagingData ->
                pagingData.map { data ->
                    data.mapData {
                        it.toTrackData()
                    }
                }
            }
    }
}