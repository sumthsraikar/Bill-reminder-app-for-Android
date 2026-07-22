package com.example.myapplicationds.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationds.data.repository.BillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: BillRepository
) : ViewModel() {

    val currency: StateFlow<String> = repository.currencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    val theme: StateFlow<String> = repository.themeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "DARK")

    val notificationEnabled: StateFlow<Boolean> = repository.notificationEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val reminderDaysBefore: StateFlow<Int> = repository.reminderDaysBeforeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val language: StateFlow<String> = repository.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    fun setCurrency(currency: String) {
        viewModelScope.launch { repository.setCurrency(currency) }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { repository.setTheme(theme) }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationEnabled(enabled) }
    }

    fun setReminderDaysBefore(days: Int) {
        viewModelScope.launch { repository.setReminderDaysBefore(days) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { repository.setLanguage(language) }
    }

    suspend fun getExportJson(): String {
        return repository.exportBackupJson()
    }

    suspend fun importBackupJson(json: String): Boolean {
        return repository.importBackupJson(json)
    }

    fun deleteAllData() {
        viewModelScope.launch {
            repository.deleteAllData()
        }
    }
}
