package com.popla.perplexitydemo.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.popla.perplexitydemo.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    
    private val viewModel: SettingsViewModel by viewModels()
    
    // UI Components
    private lateinit var modelSpinner: AutoCompleteTextView
    private lateinit var temperatureSlider: Slider
    private lateinit var temperatureValue: MaterialTextView
    private lateinit var enableWebSearchSwitch: SwitchMaterial
    private lateinit var enableCitationsSwitch: SwitchMaterial
    private lateinit var enableStreamingSwitch: SwitchMaterial
    private lateinit var saveConversationsSwitch: SwitchMaterial
    private lateinit var enableAnalyticsSwitch: SwitchMaterial
    private lateinit var clearDataButton: MaterialButton
    private lateinit var themeSpinner: AutoCompleteTextView
    private lateinit var languageSpinner: AutoCompleteTextView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupSpinners()
        setupListeners()
        observeViewModel()
    }
    
    private fun initViews(view: View) {
        modelSpinner = view.findViewById(R.id.modelSpinner)
        temperatureSlider = view.findViewById(R.id.temperatureSlider)
        temperatureValue = view.findViewById(R.id.temperatureValue)
        enableWebSearchSwitch = view.findViewById(R.id.enableWebSearchSwitch)
        enableCitationsSwitch = view.findViewById(R.id.enableCitationsSwitch)
        enableStreamingSwitch = view.findViewById(R.id.enableStreamingSwitch)
        saveConversationsSwitch = view.findViewById(R.id.saveConversationsSwitch)
        enableAnalyticsSwitch = view.findViewById(R.id.enableAnalyticsSwitch)
        clearDataButton = view.findViewById(R.id.clearDataButton)
        themeSpinner = view.findViewById(R.id.themeSpinner)
        languageSpinner = view.findViewById(R.id.languageSpinner)
    }
    
    private fun setupSpinners() {
        // AI Models
        val models = arrayOf(
            "openai/gpt-4o-mini",
            "openai/gpt-4o",
            "openai/gpt-3.5-turbo",
            "anthropic/claude-3-haiku",
            "anthropic/claude-3-sonnet",
            "google/gemini-pro",
            "meta-llama/llama-3-8b-instruct"
        )
        val modelAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, models)
        modelSpinner.setAdapter(modelAdapter)
        
        // Themes
        val themes = arrayOf("System Default", "Light", "Dark")
        val themeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, themes)
        themeSpinner.setAdapter(themeAdapter)
        
        // Languages
        val languages = arrayOf(
            "English", "Spanish", "French", "German", "Italian", 
            "Portuguese", "Russian", "Chinese", "Japanese", "Korean"
        )
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, languages)
        languageSpinner.setAdapter(languageAdapter)
    }
    
    private fun setupListeners() {
        // Model selection
        modelSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedModel = modelSpinner.adapter.getItem(position) as String
            viewModel.updateModel(selectedModel)
        }
        
        // Temperature slider
        temperatureSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                temperatureValue.text = String.format("%.1f", value)
                viewModel.updateTemperature(value)
            }
        }
        
        // Switches
        enableWebSearchSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateWebSearch(isChecked)
        }
        
        enableCitationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateCitations(isChecked)
        }
        
        enableStreamingSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateStreaming(isChecked)
        }
        
        saveConversationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateSaveConversations(isChecked)
        }
        
        enableAnalyticsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAnalytics(isChecked)
        }
        
        // Clear data button
        clearDataButton.setOnClickListener {
            showClearDataDialog()
        }
        
        // Theme selection
        themeSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedTheme = themeSpinner.adapter.getItem(position) as String
            viewModel.updateTheme(selectedTheme)
        }
        
        // Language selection
        languageSpinner.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage = languageSpinner.adapter.getItem(position) as String
            viewModel.updateLanguage(selectedLanguage)
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUI(state)
            }
        }
    }
    
    private fun updateUI(state: SettingsUiState) {
        // Update model spinner
        modelSpinner.setText(state.selectedModel, false)
        
        // Update temperature
        temperatureSlider.value = state.temperature
        temperatureValue.text = String.format("%.1f", state.temperature)
        
        // Update switches
        enableWebSearchSwitch.isChecked = state.enableWebSearch
        enableCitationsSwitch.isChecked = state.enableCitations
        enableStreamingSwitch.isChecked = state.enableStreaming
        saveConversationsSwitch.isChecked = state.saveConversations
        enableAnalyticsSwitch.isChecked = state.enableAnalytics
        
        // Update theme and language
        themeSpinner.setText(state.selectedTheme, false)
        languageSpinner.setText(state.selectedLanguage, false)
        
        // Handle data cleared
        if (state.isDataCleared) {
            Toast.makeText(context, "All data cleared successfully", Toast.LENGTH_SHORT).show()
            viewModel.clearDataClearedFlag()
        }
        
        // Handle errors
        state.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
    
    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Clear All Data")
            .setMessage("This will permanently delete all conversation history and cached data. Your settings will be preserved. This action cannot be undone.")
            .setPositiveButton("Clear Data") { _, _ ->
                viewModel.clearAllData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}