package com.popla.perplexitydemo.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popla.perplexitydemo.data.model.*
import com.popla.perplexitydemo.domain.ai.AIProcessor
import com.popla.perplexitydemo.domain.repository.ConversationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val aiProcessor: AIProcessor
) : ViewModel() {
    
    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId = _currentConversationId.asStateFlow()
    
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _searchMode = MutableStateFlow(SearchMode.GENERAL)
    val searchMode = _searchMode.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    init {
        createNewConversation()
    }
    
    fun sendMessage(content: String) {
        if (content.isBlank() || _isLoading.value) return
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val conversationId = _currentConversationId.value ?: return@launch
                
                // Add user message
                val userMessage = Message(
                    id = UUID.randomUUID().toString(),
                    conversationId = conversationId,
                    content = content,
                    role = MessageRole.USER,
                    timestamp = LocalDateTime.now()
                )
                
                conversationRepository.addMessage(userMessage)
                
                // Get conversation history for context
                val history = _messages.value.takeLast(10).map { it.content }
                
                // Process AI response
                val result = aiProcessor.processQuery(
                    query = content,
                    conversationHistory = history,
                    searchMode = _searchMode.value
                )
                
                result.fold(
                    onSuccess = { aiResponse ->
                        val assistantMessage = Message(
                            id = UUID.randomUUID().toString(),
                            conversationId = conversationId,
                            content = aiResponse.content,
                            role = MessageRole.ASSISTANT,
                            timestamp = LocalDateTime.now(),
                            citations = aiResponse.citations
                        )
                        
                        conversationRepository.addMessage(assistantMessage)
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Unknown error occurred"
                        
                        val errorMessage = Message(
                            id = UUID.randomUUID().toString(),
                            conversationId = conversationId,
                            content = "Sorry, I encountered an error processing your request.",
                            role = MessageRole.ASSISTANT,
                            timestamp = LocalDateTime.now(),
                            hasError = true
                        )
                        
                        conversationRepository.addMessage(errorMessage)
                    }
                )
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun setSearchMode(mode: SearchMode) {
        _searchMode.value = mode
    }
    
    fun createNewConversation() {
        viewModelScope.launch {
            val conversationId = UUID.randomUUID().toString()
            val conversation = Conversation(
                id = conversationId,
                title = "New Conversation",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                searchMode = _searchMode.value
            )
            
            conversationRepository.createConversation(conversation)
            _currentConversationId.value = conversationId
            
            // Observe messages for this conversation
            conversationRepository.getMessagesForConversation(conversationId)
                .collect { messageList ->
                    _messages.value = messageList
                }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}