package com.evg.api.domain.utils

typealias RootError = Error

sealed interface ServerResult<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): ServerResult<D, E>
    data class Error<out D, out E: RootError>(val error: E): ServerResult<D, E>
}