package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.SecurityUtils
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.Message
import com.example.eventHubMobile.network.RetrofitClient
import com.example.eventHubMobile.network.SendMessageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadHistory(otherUserId: Long) {
        val currentUserId = UserSession.currentUser?.id ?: return
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getChatHistory(currentUserId, otherUserId)
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.map { dto ->
                        Message(
                            id = dto.id.toInt(),
                            text = dto.text,
                            isFromMe = dto.senderId == currentUserId
                        )
                    }
                    _uiState.update { it.copy(messages = list, isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun sendMessage(receiverId: Long, text: String) {
        if (text.isBlank() || !SecurityUtils.isSafe(text)) return
        
        val currentUserId = UserSession.currentUser?.id ?: return
        
        viewModelScope.launch {
            try {

                val cleanText = SecurityUtils.sanitize(text)
                
                val request = SendMessageRequest(currentUserId, receiverId, cleanText)
                val response = RetrofitClient.apiService.sendMessage(request)
                if (response.isSuccessful) {
                    loadHistory(receiverId)
                }
            } catch (e: Exception) {

            }
        }
    }
}
