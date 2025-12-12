package com.popla.perplexitydemo.presentation.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popla.perplexitydemo.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val conversationRepository: ConversationRepository
) : ViewModel() {
    
    private val preferences: SharedPreferences = 
        context.getSharedPreferences("perplexity_settings", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        _uiState.value = SettingsUiState(
            selectedModel = preferences.getString("ai_model", "openai/gpt-4o-mini") ?: "openai/gpt-4o-mini",
            temperature = preferences.getFloat("temperature", 0.7f),
            enableWebSearch = preferences.getBoolean("enable_web_search", true),
            enableCitations = preferences.getBoolean("enable_citations", true),
            enableStreaming = preferences.getBoolean("enable_streaming", true),
            saveConversations = preferences.getBoolean("save_conversations", true),
            enableAnalytics = preferences.getBoolean("enable_analytics", false),
            selectedTheme = preferences.getString("theme", "System Default") ?: "System Default",
            selectedLanguage = preferences.getString("language", "English") ?: "English"
        )
    }
    
    fun updateModel(model: String) {
        preferences.edit().putString("ai_model", model).apply()
        _uiState.value = _uiState.value.copy(selectedModel = model)
    }
    
    fun updateTemperature(temperature: Float) {
        preferences.edit().putFloat("temperature", temperature).apply()
        _uiState.value = _uiState.value.copy(temperature = temperature)
    }
    
    fun updateWebSearch(enabled: Boolean) {
        preferences.edit().putBoolean("enable_web_search", enabled).apply()
        _uiState.value = _uiState.value.copy(enableWebSearch = enabled)
    }
    
    fun updateCitations(enabled: Boolean) {
        preferences.edit().putBoolean("enable_citations", enabled).apply()
        _uiState.value = _uiState.value.copy(enableCitations = enabled)
    }
    
    fun updateStreaming(enabled: Boolean) {
        preferences.edit().putBoolean("enable_streaming", enabled).apply()
        _uiState.value = _uiState.value.copy(enableStreaming = enabled)
    }
    
    fun updateSaveConversations(enabled: Boolean) {
        preferences.edit().putBoolean("save_conversations", enabled).apply()
        _uiState.value = _uiState.value.copy(saveConversations = enabled)
    }
    
    fun updateAnalytics(enabled: Boolean) {
        preferences.edit().putBoolean("enable_analytics", enabled).apply()
        _uiState.value = _uiState.value.copy(enableAnalytics = enabled)
    }
    
    fun updateTheme(theme: String) {
        preferences.edit().putString("theme", theme).apply()
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
    }
    
    fun updateLanguage(language: String) {
        preferences.edit().putString("language", language).apply()
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
    }
    
    fun clearAllData() {
        viewModelScope.launch {
            try {
                // Clear conversation data
                conversationRepository.deleteAllConversations()
                
                // Clear preferences (except settings)
                val settingsToKeep = mapOf(
                    "ai_model" to _uiState.value.selectedModel,
                    "temperature" to _uiState.value.temperature,
                    "enable_web_search" to _uiState.value.enableWebSearch,
                    "enable_citations" to _uiState.value.enableCitations,
                    "enable_streaming" to _uiState.value.enableStreaming,
                    "save_conversations" to _uiState.value.saveConversations,
                    "enable_analytics" to _uiState.value.enableAnalytics,
                    "theme" to _uiState.value.selectedTheme,
                    "language" to _uiState.value.selectedLanguage
                )
                
                preferences.edit().clear().apply()
                
                // Restore settings
                val editor = preferences.edit()
                settingsToKeep.forEach { (key, value) ->
                    when (value) {
                        is String -> editor.putString(key, value)
                        is Float -> editor.putFloat(key, value)
                        is Boolean -> editor.putBoolean(key, value)
                    }
                }
                editor.apply()
                
                _uiState.value = _uiState.value.copy(isDataCleared = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to clear data: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearDataClearedFlag() {
        _uiState.value = _uiState.value.copy(isDataCleared = false)
    }
}

data class SettingsUiState(
    val selectedModel: String = "openai/gpt-4o-mini",
    val temperature: Float = 0.7f,
    val enableWebSearch: Boolean = true,
    val enableCitations: Boolean = true,
    val enableStreaming: Boolean = true,
    val saveConversations: Boolean = true,
    val enableAnalytics: Boolean = false,
    val selectedTheme: String = "System Default",
    val selectedLanguage: String = "English",
    val isDataCleared: Boolean = false,
    val error: String? = null
)