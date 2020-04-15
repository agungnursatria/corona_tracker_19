package tech.awesome.coronatrack.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tech.awesome.coronatrack.BuildConfig
import tech.awesome.network.Api
import tech.awesome.network.CacheProvider
import tech.awesome.network.ICacheProvider
import tech.awesome.utils.NetworkConstant
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        MoshiConverterFactory.create()
    }

    single {
        ICacheProvider(androidContext().cacheDir) as CacheProvider
    }

    single {
        OkHttpClient().newBuilder()
            .connectTimeout(NetworkConstant.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConstant.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConstant.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .cache(get<CacheProvider>().cache)
            .addInterceptor(StethoInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addInterceptor(get<CacheProvider>().getInterceptor())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(get<MoshiConverterFactory>())
            .build()
    }

    single {
        get<Retrofit>().create(Api::class.java)
    }

}