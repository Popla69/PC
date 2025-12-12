package com.popla.perplexitydemo.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popla.perplexitydemo.data.model.SearchMode
import com.popla.perplexitydemo.domain.ai.AIProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val aiProcessor: AIProcessor
) : ViewModel() {
    
    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentSearchMode = MutableStateFlow(SearchMode.GENERAL)
    val currentSearchMode: StateFlow<SearchMode> = _currentSearchMode.asStateFlow()
    
    private val _pageSummary = MutableStateFlow<String?>(null)
    val pageSummary: StateFlow<String?> = _pageSummary.asStateFlow()
    
    fun setSearchMode(mode: SearchMode) {
        _currentSearchMode.value = mode
    }
    
    fun performSearch(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val searchPrompt = buildSearchPrompt(query, _currentSearchMode.value)
                val result = aiProcessor.processQuery(
                    query = searchPrompt,
                    searchMode = _currentSearchMode.value
                )
                
                result.onSuccess { response ->
                    // Parse AI response into search results
                    val results = parseSearchResults(response.content, query)
                    _searchResults.value = results
                }.onFailure { error ->
                    // Handle search error
                    _searchResults.value = emptyList()
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun generatePageSummary(url: String) {
        viewModelScope.launch {
            try {
                val summaryPrompt = "Please provide a concise summary of the webpage at: $url. " +
                        "Focus on the main points, key information, and important details. " +
                        "Keep the summary informative but brief."
                
                val result = aiProcessor.processQuery(
                    query = summaryPrompt,
                    searchMode = SearchMode.ACADEMIC
                )
                
                result.onSuccess { response ->
                    _pageSummary.value = response.content
                }.onFailure { error ->
                    _pageSummary.value = "Unable to generate summary: ${error.message}"
                }
            } catch (e: Exception) {
                _pageSummary.value = "Error generating summary"
            }
        }
    }
    
    private fun buildSearchPrompt(query: String, mode: SearchMode): String {
        return when (mode) {
            SearchMode.ACADEMIC -> "Provide academic and scholarly information about: $query. " +
                    "Include research findings, academic sources, and scientific perspectives."
            SearchMode.GENERAL -> "Search for comprehensive information about: $query. " +
                    "Provide a well-rounded overview with multiple perspectives."
            SearchMode.PROGRAMMING -> "Find programming and technical information about: $query. " +
                    "Include code examples, documentation, and developer resources."
            SearchMode.MATH -> "Provide mathematical information and explanations about: $query. " +
                    "Include formulas, calculations, and mathematical concepts."
            SearchMode.CREATIVE -> "Find creative and innovative information about: $query. " +
                    "Include artistic, design, and creative perspectives."
            SearchMode.WRITING -> "Provide writing and editorial information about: $query. " +
                    "Include style guides, writing tips, and editorial perspectives."
        }
    }
    
    private fun parseSearchResults(aiResponse: String, originalQuery: String): List<SearchResult> {
        // Parse AI response into structured search results
        // This is a simplified implementation - in a real app you'd have more sophisticated parsing
        return listOf(
            SearchResult(
                title = "AI-Generated Insights: $originalQuery",
                url = "ai://generated-content",
                snippet = aiResponse.take(200) + if (aiResponse.length > 200) "..." else "",
                source = "AI Analysis"
            )
        )
    }
}

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String,
    val source: String
)