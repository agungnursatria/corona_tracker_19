package tech.awesome.domain.pref

import tech.awesome.utils.PrefKey
import tech.awesome.utils.Saver

class IAppPref : AppPref {
    override fun setCountryPrimaryKey(countryName: String) =
        Saver.instance().saveString(PrefKey.Country, countryName)

    override fun getCountryName(): String? = Saver.instance().getString(PrefKey.Country)

    override fun setColorMode(isNightMode: Boolean) = Saver.instance().saveBoolean(PrefKey.NightMode, isNightMode)

    override fun getColorMode(): Boolean = Saver.instance().getBoolean(PrefKey.NightMode, false)

    override fun setFirstTime(isFirstTime: Boolean) = Saver.instance().saveBoolean(PrefKey.IsFirstTime, isFirstTime)

    override fun getFirstTime(): Boolean = Saver.instance().getBoolean(PrefKey.IsFirstTime, true)
}