package tech.awesome.network

import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit

interface CacheProvider {
    fun getInterceptor(): Interceptor
    val cache: Cache
}