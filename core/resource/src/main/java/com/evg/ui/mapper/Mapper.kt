package com.evg.ui.mapper

import android.content.Context
import com.evg.api.domain.utils.NetworkError
import com.evg.resource.R

fun NetworkError.toErrorMessage(context: Context): String {
    return when (this) {
        NetworkError.REQUEST_TIMEOUT -> context.getString(R.string.error_request_timeout)
        NetworkError.TOO_MANY_REQUESTS -> context.getString(R.string.error_too_many_requests)
        NetworkError.FORBIDDEN -> context.getString(R.string.error_forbidden)
        NetworkError.NOT_FOUND -> context.getString(R.string.error_not_found)
        NetworkError.SERVER_ERROR -> context.getString(R.string.error_server)
        NetworkError.SERIALIZATION -> context.getString(R.string.error_serialization)
        NetworkError.UNKNOWN_HOST -> context.getString(R.string.error_unknown_host)
        NetworkError.PROTOCOL_EXCEPTION -> context.getString(R.string.error_protocol)
        NetworkError.CONNECT_EXCEPTION -> context.getString(R.string.error_connection)
        NetworkError.UNKNOWN -> context.getString(R.string.error_unknown)
    }
}
