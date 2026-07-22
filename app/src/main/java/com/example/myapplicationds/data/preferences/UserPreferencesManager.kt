package com.example.myapplicationds.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "billbuddy_settings")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferenceKeys {
        val CURRENCY = stringPreferencesKey("currency")
        val THEME = stringPreferencesKey("theme")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val REMINDER_DAYS_BEFORE = intPreferencesKey("reminder_days_before")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val currencyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.CURRENCY] ?: "₹"
    }

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.THEME] ?: "DARK"
    }

    val notificationEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.NOTIFICATION_ENABLED] ?: true
    }

    val reminderDaysBeforeFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.REMINDER_DAYS_BEFORE] ?: 1
    }

    val languageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LANGUAGE] ?: "English"
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CURRENCY] = currency
        }
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME] = theme
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setReminderDaysBefore(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.REMINDER_DAYS_BEFORE] = days
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
