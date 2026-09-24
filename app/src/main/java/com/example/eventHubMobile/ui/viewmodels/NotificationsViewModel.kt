package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.NotificationItem
import com.example.eventHubMobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        val userId = UserSession.currentUser?.id ?: return
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getNotifications(userId)
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.map { dto ->
                        NotificationItem(
                            id = dto.id.toInt(),
                            title = dto.title,
                            message = dto.message,
                            date = dto.createdAt ?: "Наскоро"
                        )
                    }
                    _uiState.update { it.copy(notifications = list, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Грешка при зареждане") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteNotification(id)
                if (response.isSuccessful) {
                    
                    loadNotifications()
                }
            } catch (e: Exception) {
                
            }
        }
    }
}
