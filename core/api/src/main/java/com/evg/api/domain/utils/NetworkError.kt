package com.evg.api.domain.utils

enum class NetworkError: Error {
    REQUEST_TIMEOUT,
    TOO_MANY_REQUESTS,
    FORBIDDEN,
    NOT_FOUND,
    SERVER_ERROR,
    SERIALIZATION,
    UNKNOWN_HOST,
    PROTOCOL_EXCEPTION,
    CONNECT_EXCEPTION,
    UNKNOWN,
}

class NetworkErrorException(error: NetworkError) : Exception("Network error: $error")