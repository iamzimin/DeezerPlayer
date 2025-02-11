package com.evg.api.domain.utils

enum class NetworkError: Error {
    REQUEST_TIMEOUT,
    TOO_MANY_REQUESTS,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN,
}

class NetworkErrorException(error: NetworkError) : Exception("Network error: $error")