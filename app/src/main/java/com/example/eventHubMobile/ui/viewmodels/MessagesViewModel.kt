package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.ChatSummary
import com.example.eventHubMobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessagesUiState(
    val chats: List<ChatSummary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MessagesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        val userId = UserSession.currentUser?.id ?: return
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getConversations(userId)
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.map { dto ->
                        ChatSummary(
                            id = dto.otherUserId.toInt(),
                            userName = dto.otherUserName,
                            lastMessage = dto.lastMessage,
                            time = dto.lastMessageTime
                        )
                    }
                    _uiState.update { it.copy(chats = list, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Грешка при зареждане на чатовете") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
