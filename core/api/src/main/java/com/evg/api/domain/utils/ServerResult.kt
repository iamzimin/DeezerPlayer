package com.evg.api.domain.utils

typealias RootError = Error

/**
 * Результат выполнения сетевого запроса, содержащий либо данные, либо ошибку
 *
 * @param D Тип данных в случае успешного результата
 * @param E Тип ошибки, наследуемый от [RootError]
 */
sealed interface ServerResult<out D, out E: RootError> {
    /**
     * Успешный результат запроса
     *
     * @param data Полученные данные
     */
    data class Success<out D, out E: RootError>(val data: D): ServerResult<D, E>

    /**
     * Ошибка при выполнении запроса
     *
     * @param error Тип ошибки, возникшей при запросе
     */
    data class Error<out D, out E: RootError>(val error: E): ServerResult<D, E>
}

/**
 * Преобразует данные в [ServerResult], оставляя ошибку без изменений
 *
 * @param transform Функция преобразования успешного результата
 * @return Новый [ServerResult] с преобразованными данными или той же ошибкой
 */
inline fun <D, E : RootError, R> ServerResult<D, E>.mapData(transform: (D) -> R): ServerResult<R, E> {
    return when (this) {
        is ServerResult.Success -> ServerResult.Success(transform(this.data))
        is ServerResult.Error -> ServerResult.Error(this.error)
    }
}