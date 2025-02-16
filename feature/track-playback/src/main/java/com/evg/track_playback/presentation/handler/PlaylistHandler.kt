package com.evg.track_playback.presentation.handler

import com.evg.track_playback.domain.model.TrackData

/**
 * Управляет плейлистом
 */
class PlaylistHandler {
    private var playlist: List<TrackData> = emptyList()

    /**
     * Устанавливает новый плейлист
     *
     * @param tracks Список треков для установки
     */
    fun setPlaylist(tracks: List<TrackData>) {
        playlist = tracks
    }

    /**
     * Возвращает текущий плейлист
     *
     * @return Список треков в плейлисте
     */
    fun getPlaylist(): List<TrackData> = playlist

    /**
     * Ищет трек в плейлисте по идентификатору
     *
     * @param trackId Идентификатор трека
     * @return Найденный трек или null, если трек не найден
     */
    fun findTrackById(trackId: Long): TrackData? =
        playlist.find { it.trackID == trackId }

    /**
     * Ищет трек в плейлисте по строковому идентификатору
     *
     * @param trackId Строковый идентификатор трека
     * @return Найденный трек или null, если идентификатор некорректен или трек не найден
     */
    fun findTrackById(trackId: String): TrackData? {
        val id = trackId.toLongOrNull() ?: return null
        return findTrackById(id)
    }

    /**
     * Удаляет трек из плейлиста и вычисляет новый индекс текущего трека
     *
     * @param track Трек, который необходимо удалить
     * @return Обновленный список треков и новый индекс текущего трека (или null, если плейлист пуст)
     */
    fun removeTrackAndComputeIndex(track: TrackData): Pair<List<TrackData>, Int?> {
        val removedIndex = playlist.indexOfFirst { it.trackID == track.trackID }
        val updatedList = playlist.filterNot { it.trackID == track.trackID }
        val newCurrentIndex = when {
            updatedList.isEmpty() -> null
            removedIndex == playlist.lastIndex -> updatedList.lastIndex
            else -> removedIndex
        }
        playlist = updatedList
        return updatedList to newCurrentIndex
    }

    /**
     * Переключает статус загрузки трека
     *
     * @param track Трек, статус которого необходимо изменить
     * @param isDownloaded Текущий статус загрузки (true — скачан, false — не скачан)
     */
    fun toggleTrackDownloadStatus(track: TrackData, isDownloaded: Boolean) {
        playlist = playlist.map { currentTrack ->
            if (currentTrack.trackID == track.trackID)
                currentTrack.copy(isDownloaded = !isDownloaded)
            else
                currentTrack
        }
    }
}
