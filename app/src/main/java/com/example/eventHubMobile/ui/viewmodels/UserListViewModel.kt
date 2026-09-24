package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.model.User
import com.example.eventHubMobile.data.model.UserRole
import com.example.eventHubMobile.network.RetrofitClient
import com.example.eventHubMobile.network.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserListUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UserListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    val mappedUsers = response.body()!!.map { it.toDomain() }
                    _uiState.update { it.copy(users = mappedUsers, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Грешка при зареждане на потребители") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

fun UserDto.toDomain(): User {
    return User(
        id = this.id ?: 0L,
        name = this.name,
        email = this.email,
        role = UserRole.fromCode(this.role)
    )
}
