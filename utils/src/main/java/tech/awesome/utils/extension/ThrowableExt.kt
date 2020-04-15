package tech.awesome.utils.extension

import retrofit2.HttpException
import tech.awesome.utils.NetworkConstant
import java.io.IOException
import java.net.SocketTimeoutException

fun Throwable.getError(): String =
    when (this) {
        is HttpException -> NetworkConstant.OFFLINE_MESSAGE
        is SocketTimeoutException -> NetworkConstant.ERROR_MESSAGE
        is IOException -> NetworkConstant.ERROR_MESSAGE
        else -> localizedMessage ?: NetworkConstant.ERROR_MESSAGE
    }
