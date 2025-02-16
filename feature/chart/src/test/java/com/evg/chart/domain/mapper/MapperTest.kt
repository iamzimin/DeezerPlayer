package com.evg.chart.domain.mapper

import com.evg.api.domain.models.AlbumResponse
import com.evg.api.domain.models.ArtistResponse
import com.evg.api.domain.models.TrackResponse
import com.evg.chart.domain.model.TrackData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class TrackResponseToTrackDataTest {

    @Test
    fun `the test should return a correctly converted TrackResponse to TrackData`() {
        // Given
        val trackResponse = TrackResponse(
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

        val expected = TrackData(
            trackID = 123,
            trackTitle = "Test Track",
            artistName = "Test Artist",
            albumCover = "http://example.com/cover_big.jpg"
        )

        // When
        val actual = trackResponse.toTrackData()

        // Then
        Assertions.assertEquals(expected, actual)
    }
}