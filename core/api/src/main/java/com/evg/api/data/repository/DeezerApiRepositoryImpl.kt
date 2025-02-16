package com.evg.api.data.repository

import com.evg.api.domain.models.ChartResponse
import com.evg.api.domain.models.SearchTrackResponse
import com.evg.api.domain.models.TrackResponse
import com.evg.api.domain.models.WrappedAlbumData
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.service.DeezerApi
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.google.gson.JsonParseException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Реализация репозитория для работы с API Deezer
 *
 * @param deezerRetrofit Экземпляр Retrofit для выполнения сетевых запросов
 */
class DeezerApiRepositoryImpl(
    deezerRetrofit: Retrofit,
): DeezerApiRepository {
    private val deezerApi = deezerRetrofit.create(DeezerApi::class.java)

    /**
     * Выполняет безопасный вызов API, возвращая результат [ServerResult] с данными или ошибкой [NetworkError]
     *
     * @param apiCall Лямбда, выполняющая API-запрос
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): ServerResult<T, NetworkError> {
        return try {
            ServerResult.Success(apiCall())
        } catch (e: JsonParseException) {
            ServerResult.Error(NetworkError.SERIALIZATION)
        } catch (e: HttpException) {
            when (e.code()) {
                403 -> ServerResult.Error(NetworkError.FORBIDDEN)
                404 -> ServerResult.Error(NetworkError.NOT_FOUND)
                408 -> ServerResult.Error(NetworkError.REQUEST_TIMEOUT)
                429 -> ServerResult.Error(NetworkError.TOO_MANY_REQUESTS)
                in 500..599 -> ServerResult.Error(NetworkError.SERVER_ERROR)
                else -> ServerResult.Error(NetworkError.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            ServerResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: UnknownHostException) {
            ServerResult.Error(NetworkError.UNKNOWN_HOST)
        } catch (e: ProtocolException) {
            ServerResult.Error(NetworkError.PROTOCOL_EXCEPTION)
        } catch (e: ConnectException) {
            ServerResult.Error(NetworkError.CONNECT_EXCEPTION)
        } catch (e: Exception) {
            ServerResult.Error(NetworkError.UNKNOWN)
        }
    }

    /**
     * Получает список чартов
     *
     * @return Результат запроса с данными [ChartResponse] или ошибкой [NetworkError]
     */
    override suspend fun getChart(): ServerResult<ChartResponse, NetworkError> {
        return safeApiCall { deezerApi.getChart() }
    }

    /**
     * Выполняет поиск треков по запросу и индексу
     *
     * @param query Запрос для поиска треков
     * @param index Индекс результатов
     * @return Результат запроса с данными [SearchTrackResponse] или ошибкой [NetworkError]
     */
    override suspend fun searchTrackByPage(query: String, index: Int): ServerResult<SearchTrackResponse, NetworkError> {
        if (query.isBlank()) {
            return ServerResult.Success(
                SearchTrackResponse(
                    data = null,
                    prev = null,
                    next = null,
                )
            )
        }
        return safeApiCall { deezerApi.searchTrack(query = query, index = index) }
    }

    /**
     * Получает список треков альбома по идентификатору трека
     *
     * @param id Идентификатор трека
     * @return Результат запроса со списком [TrackResponse] или ошибкой [NetworkError]
     */
    override suspend fun getAlbumByTrackId(id: Long): ServerResult<List<TrackResponse>, NetworkError> {
        return safeApiCall {
            val trackResponse: TrackResponse = deezerApi.getTrackById(id = id)
            val albumResponse: WrappedAlbumData = deezerApi.getAlbumById(id = trackResponse.album.id)

            val albumTrackIds = albumResponse.data.map { it.id }
            val otherTrackIds = albumTrackIds.filter { it != trackResponse.id }

            val trackResponses = coroutineScope {
                otherTrackIds.map { trackId ->
                    async { deezerApi.getTrackById(trackId) }
                }.awaitAll()
            }

            val allTracks = listOf(trackResponse) + trackResponses
            val sortedTracks = allTracks.sortedBy { track ->
                albumResponse.data.indexOfFirst { album -> album.id == track.id }
            }

            sortedTracks
        }
    }
}