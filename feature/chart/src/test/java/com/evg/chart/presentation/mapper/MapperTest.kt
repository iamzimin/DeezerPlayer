package com.evg.chart.presentation.mapper

import com.evg.chart.domain.model.TrackData
import com.evg.ui.model.TrackTileContent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TrackDataToTrackTileContentTest {

    @Test
    fun `the test should return a correctly converted TrackData to TrackTileContent`() {
        // Given
        val trackData = TrackData(
            trackID = 123,
            trackTitle = "Song Title",
            artistName = "Artist Name",
            albumCover = "http://example.com/cover.jpg"
        )

        val expected = TrackTileContent(
            trackID = 123,
            trackTitle = "Song Title",
            artistName = "Artist Name",
            albumCover = "http://example.com/cover.jpg"
        )

        // When
        val actual = trackData.toTrackTileContent()

        // Then
        Assertions.assertEquals(expected, actual)
    }
}