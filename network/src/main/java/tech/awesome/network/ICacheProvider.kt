package tech.awesome.network

import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit

class ICacheProvider(private val cacheDir: File): CacheProvider, Interceptor {
    private val cacheControl by lazy {
        CacheControl.Builder()
            .maxStale(1, TimeUnit.HOURS)
            .maxAge(1, TimeUnit.HOURS)
            .build()
    }

    override val cache by lazy {
        Cache(cacheDir, 10 * 1024 * 1024)
    }

    override fun getInterceptor(): Interceptor {
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        return response.newBuilder()
            .removeHeader("Cache-Control")
            .header("Cache-Control", cacheControl.toString())
            .build();
    }

}