package tech.awesome.domain.pref

interface AppPref {
    fun setCountryPrimaryKey(countryName: String)
    fun getCountryName() : String?
    fun setColorMode(isNightMode: Boolean)
    fun getColorMode() : Boolean
    fun setFirstTime(isFirstTime: Boolean)
    fun getFirstTime() : Boolean
}