package ed.maevski.minideviantart.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    //Нам нужен контекст приложения
    private val appContext = context.applicationContext
    //Создаем экземпляр SharedPreferences
    private val preference: SharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    init {
        //Логика для первого запуска приложения, чтобы положить наши настройки,
        //Сюда потом можно добавить и другие настройки
        if(preference.getBoolean(KEY_FIRST_LAUNCH, false)) {
            preference.edit { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    //Category prefs
    //Сохраняем категорию
    fun saveDefaultCategory(category: String) {
        preference.edit { putString(KEY_DEFAULT_CATEGORY, category) }
    }
    //Забираем категорию
    fun getDefaultCategory(): String {
        return preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    }

    //Token prefs
    //Сохраняем категорию
    fun saveToken(token: String) {
        preference.edit { putString(KEY_ACCESS_TOKEN, token).apply() }
    }
    //Забираем категорию
    fun getToken(): String {
        return preference.getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

    fun saveTrialPeriodStart(timeTrialPeriodStart: Long) {
        preference.edit { putLong(KEY_TRIAL_PERIOD_START, timeTrialPeriodStart).apply() }
    }

    fun getTrialPeriodStart(): Long {
        return preference.getLong(KEY_TRIAL_PERIOD_START, 0L)
    }

    //Ключи для наших настроек, по ним мы их будем получать
    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_TRIAL_PERIOD_START = "trialPeriodStart"
        private const val DEFAULT_CATEGORY = "newest"
    }
}