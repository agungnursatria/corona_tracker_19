package tech.awesome.coronatrack

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.stetho.Stetho
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import tech.awesome.coronatrack.di.*
import tech.awesome.utils.Saver
import timber.log.Timber

class Application : MultiDexApplication() {
//    private val viewPump: ViewPump by inject()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }

        Saver.init(this)

        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@Application)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    persistenceModule,
                    repositoryModule,
                    viewModelModule
                )
            )
        }
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Menlo-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        operator fun get(context: Context): Application {
            return context.applicationContext as Application
        }
    }
}