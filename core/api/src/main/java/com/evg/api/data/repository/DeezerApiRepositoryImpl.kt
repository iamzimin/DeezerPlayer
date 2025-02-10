package com.evg.api.data.repository

import com.evg.api.domain.models.ChartResponse
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.service.DeezerApi
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.google.gson.JsonParseException
import retrofit2.HttpException
import retrofit2.Retrofit
import java.net.SocketTimeoutException

class DeezerApiRepositoryImpl(
    deezerRetrofit: Retrofit,
): DeezerApiRepository {
    private val deezerApi = deezerRetrofit.create(DeezerApi::class.java)

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): ServerResult<T, NetworkError> {
        return try {
            ServerResult.Success(apiCall())
        } catch (e: JsonParseException) {
            ServerResult.Error(NetworkError.SERIALIZATION)
        } catch (e: HttpException) {
            when (e.code()) {
                408 -> ServerResult.Error(NetworkError.REQUEST_TIMEOUT)
                429 -> ServerResult.Error(NetworkError.TOO_MANY_REQUESTS)
                in 500..599 -> ServerResult.Error(NetworkError.SERVER_ERROR)
                else -> ServerResult.Error(NetworkError.UNKNOWN)
            }
        } catch (e: SocketTimeoutException) {
            ServerResult.Error(NetworkError.REQUEST_TIMEOUT)
        } catch (e: Exception) {
            ServerResult.Error(NetworkError.UNKNOWN)
        }
    }

    override suspend fun getChart(): ServerResult<ChartResponse, NetworkError> {
        return safeApiCall {
            val response = deezerApi.getChart()

            // TODO db

            response
        }
    }
}