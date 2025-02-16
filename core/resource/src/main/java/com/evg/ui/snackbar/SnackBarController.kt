package com.evg.ui.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Событие для отображения Snackbar
 *
 * @param message Текст сообщения
 * @param action Действие, выполняемое при нажатии на кнопку в Snackbar
 */
data class SnackBarEvent(
    val message: String,
    val action: SnackBarAction? = null,
)

/**
 * Действие, выполняемое при взаимодействии с Snackbar
 *
 * @param name Название действия
 * @param action Функция, вызываемая при нажатии
 */
data class SnackBarAction(
    val name: String,
    val action: () -> Unit,
)

/**
 * Контроллер для управления событиями Snackbar
 */
object SnackBarController {
    private val _events = Channel<SnackBarEvent>()
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackBarEvent) {
        _events.send(event)
    }
}