package tech.awesome.coronatrack.ui.splashscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.koin.android.ext.android.inject
import tech.awesome.coronatrack.R
import tech.awesome.coronatrack.ui.main.MainActivity
import tech.awesome.domain.pref.AppPref
import tech.awesome.utils.extension.setAssetImage

class SplashActivity : AppCompatActivity() {
    private val pref by inject<AppPref>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isFirstTime = pref.getFirstTime()
        if (isFirstTime) {
            pref.setFirstTime(false)
            pref.setColorMode(true)
        }

        val isNightMode = pref.getColorMode()
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        recreate()
    }

    override fun onStart() {
        super.onStart()
        startActivity(MainActivity.getIntent(this))
        finish()
    }
}
