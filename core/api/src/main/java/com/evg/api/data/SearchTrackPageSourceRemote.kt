package com.evg.api.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.evg.api.domain.models.TrackResponse
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.NetworkErrorException
import com.evg.api.domain.utils.ServerResult
import javax.inject.Inject

class SearchTrackPageSourceRemote @Inject constructor(
    private val apiRepository: DeezerApiRepository,
): PagingSource<Int, ServerResult<TrackResponse, NetworkError>>() {
    var query = ""

    override fun getRefreshKey(state: PagingState<Int, ServerResult<TrackResponse, NetworkError>>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(25) ?: page.nextKey?.minus(25)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ServerResult<TrackResponse, NetworkError>> {
        val index = params.key ?: 0

        val response = apiRepository.searchTrackByPage(
            query = query,
            index = index,
        )

        return when (response) {
            is ServerResult.Success -> {
                val data = response.data.data ?: return LoadResult.Error(Exception("Data is null"))

                return LoadResult.Page(
                    data = data.map { ServerResult.Success(it) },
                    prevKey = getIndexFromUrl(response.data.prev),
                    nextKey = getIndexFromUrl(response.data.next),
                )
            }
            is ServerResult.Error -> {
                LoadResult.Page(
                    data = listOf(ServerResult.Error(response.error)),
                    prevKey = null,
                    nextKey = null,
                )
            }
        }
    }

    private fun getIndexFromUrl(url: String?): Int? {
        val regex = """[?&]index=(\d+)""".toRegex()
        return url?.let { regex.find(it)?.groupValues?.get(1)?.toIntOrNull() }
    }
}