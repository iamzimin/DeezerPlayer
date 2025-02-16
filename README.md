# Deezer Player

Стек: Kotlin, Clean Architecture, Jetpack Compose, Coroutine, MVI, Retrofit, Gson, Room, Dagger Hilt, Jetpack Navigation, Paging, Media, Firebase Crashlytics, JUnit.
<br><br>

## Инструкция по запуску
- [Скачать apk](https://github.com/iamzimin/DeezerPlayer/releases/latest) файл и установить.
<br><br>

## Функционал приложения
1. **Cкачанных треки.** Экран с скачанными треками и возможностью поиска по списку.

<br>

2. **Треки из API.** Экран с треками из [DeezerAPI](https://developers.deezer.com/api) и возможностью поиска по списку.
 - При запуске выполняется запрос на получание [треков из чарта](https://api.deezer.com/chart). 
 - После при поиске используется запрос на [поиск треков](https://api.deezer.com/search?q=query), выполняемый с использованием пагинации.
 - При выборе трека выполняется запрос на [получении трека](https://api.deezer.com/track/781592622) и всех треков из данного альбома, генеруя плейлист.

<br>

3. **Воспроизведение трека.** Экран воспроизведения треков на котором можно:
 - Переключать треки внутри альбома (если переход был с экрана треков из API).
 - Переключать скачанные треки (если пеерход был с экрана скачанных треков).
 - Ставить трек на паузу, смотреть прогресс его воспроизведения, текущее состояние трека в формате mm:ss.
 - Просматривать название трека, альбома, имени исполнителя, обложки трека.
 - Скачаивать трек.

<br>

4. **Фоновый плеер.** Фоновый плеер позволяет управлять воспроизведением текущего плейлиста, который был установлен на экране воспроизведения трека.
<br><br>

## Скриншоты

<img src="https://github.com/user-attachments/assets/9afda54b-8517-4426-ad19-c9c539a36e65" alt="Image1" width="200"/>
<img src="https://github.com/user-attachments/assets/a14b49bd-ab34-4b6d-b85f-61f903611ebc" alt="Image2" width="200"/>
<img src="https://github.com/user-attachments/assets/2c11119b-e220-449b-a1f3-51552f9e3eba" alt="Image3" width="200"/>
<img src="https://github.com/user-attachments/assets/03cbd43a-95d8-4709-ad7a-93543bfbd4a3" alt="Image4" width="200"/>
<img src="https://github.com/user-attachments/assets/7175bb36-a996-4a31-bf7a-053a152433c2" alt="Image5" width="200"/>
<img src="https://github.com/user-attachments/assets/93105a48-eced-4c58-b431-ad079454ccbf" alt="Image6" width="200"/>
<img src="https://github.com/user-attachments/assets/7a54262b-6640-4cb9-a634-e663f1793b15" alt="Image7" width="200"/>
<img src="https://github.com/user-attachments/assets/09f185df-395e-463c-bc95-16315d0cd7e5" alt="Image8" width="200"/>

