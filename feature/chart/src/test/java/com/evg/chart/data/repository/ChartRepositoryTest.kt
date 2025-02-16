package com.evg.chart.data.repository

import com.evg.api.data.SearchTrackPageSourceRemote
import com.evg.api.domain.models.AlbumResponse
import com.evg.api.domain.models.ArtistResponse
import com.evg.api.domain.models.ChartResponse
import com.evg.api.domain.models.SearchTrackResponse
import com.evg.api.domain.models.TrackResponse
import com.evg.api.domain.models.WrapperTrackData
import com.evg.api.domain.repository.DeezerApiRepository
import com.evg.api.domain.utils.NetworkError
import com.evg.api.domain.utils.ServerResult
import com.evg.chart.domain.mapper.toTrackData
import com.evg.chart.domain.model.TrackData
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FakeDeezerApiRepository : DeezerApiRepository {
    var chartResult: ServerResult<ChartResponse, NetworkError>? = null

    override suspend fun getChart(): ServerResult<ChartResponse, NetworkError> {
        return chartResult ?: ServerResult.Error(NetworkError.SERVER_ERROR)
    }

    override suspend fun searchTrackByPage(
        query: String,
        index: Int
    ): ServerResult<SearchTrackResponse, NetworkError> { TODO("Not yet implemented") }

    override suspend fun getAlbumByTrackId(id: Long): ServerResult<List<TrackResponse>, NetworkError> { TODO("Not yet implemented") }
}

class ChartRepositoryImplTest {

    private lateinit var chartRepository: ChartRepositoryImpl
    private lateinit var fakeApiRepository: FakeDeezerApiRepository
    private lateinit var fakeSearchTrackPageSourceRemote: SearchTrackPageSourceRemote

    @BeforeEach
    fun setup() {
        fakeApiRepository = FakeDeezerApiRepository()
        fakeSearchTrackPageSourceRemote = SearchTrackPageSourceRemote(fakeApiRepository)
        chartRepository = ChartRepositoryImpl(fakeApiRepository, fakeSearchTrackPageSourceRemote)
    }

    @Test
    fun `getChartTracks should return the correct track list`() = runBlocking {
        // Given
        val dummyTrackResponse = TrackResponse(
            id = 123,
            title = "Test Track",
            artist = ArtistResponse(
                name = "Test Artist",
            ),
            preview = "http://example.com/preview.jpg",
            album = AlbumResponse(
                id = 1,
                title = "Test Album",
                cover = "http://example.com/cover_big.jpg"
            )
        )
        val dummyChartResponse = ChartResponse(tracks = WrapperTrackData(data = listOf(dummyTrackResponse)))
        fakeApiRepository.chartResult = ServerResult.Success(dummyChartResponse)
        val expected = listOf(dummyTrackResponse.toTrackData())

        // When
        val result = chartRepository.getChartTracks()

        // Then
        Assertions.assertEquals(ServerResult.Success<List<TrackData>, NetworkError>(expected), result)
    }
}