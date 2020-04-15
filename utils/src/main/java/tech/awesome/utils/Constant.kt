package tech.awesome.utils

object NetworkConstant {
    const val NETWORK_TIMEOUT = 60L
    const val ERROR_MESSAGE = "Cannot proceed your request, please try again later"
    const val OFFLINE_MESSAGE = "No connection, turn your connection active to process"
}

object FormatConstant {
    const val FORMAT_DATE_LATEST_UPDATE = "dd MMMM yyyy, HH.mm"
    const val FORMAT_DATE = "dd MMMM yyyy"
    const val FORMAT_DATE_MONTH = "MMM"
    const val FORMAT_DATE_SERVER_FULL = "yyyy-MM-dd'T'HH:mm:ss"
    const val FORMAT_DATE_SERVER_FULL_2 = "yyyy-MM-dd HH:mm:ss"
    const val FORMAT_DATE_SERVER = "yyyy-MM-dd"
    const val FORMAT_DATE_DAILY_PATH = "MM-dd-yyyy"
}

object IntentRequestConstant {
    const val ADD_COUNTRY = 0
}
object ExtraConstant {
    const val EXTRA_URL = "extra_url"
    const val EXTRA_OVERVIEW = "extra_overview"
    const val EXTRA_DAILY_SUMMARY = "extra_dailys_summary"
    const val EXTRA_DATE = "extra_date"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_COUNTRY = "extra_country"
    const val EXTRA_CONFIRMED = "extra_confirmed"
    const val EXTRA_STATUS = "extra_status"
    const val EXTRA_LATLNG = "extra_latlng"
}

object PrefKey {
    const val PREF_NAME = "CoronaPref"
    const val Country = "pref_country"
    const val NightMode = "pref_night_mode"
    const val IsFirstTime = "pref_is_first_time"
}

object StatusConstant {
    const val CONFIRMED = 0
    const val RECOVERED = 1
    const val DEATHS = 2
}