package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.SecurityUtils
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.network.RetrofitClient
import com.example.eventHubMobile.network.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false
)

class EditProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentProfile()
    }

    fun loadCurrentProfile() {
        val user = UserSession.currentUser
        if (user != null) {
            _uiState.update {
                it.copy(
                    name = user.name,
                    email = user.email,
                    phone = user.phone ?: ""
                )
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName, error = null) }
    }

    fun onPhoneChange(newPhone: String) {
        _uiState.update { it.copy(phone = newPhone, error = null) }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val user = UserSession.currentUser ?: return
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Името е задължително") }
            return
        }

        val cleanName = SecurityUtils.sanitize(state.name)
        val cleanPhone = SecurityUtils.sanitize(state.phone)

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val userDto = UserDto(
                    id = user.id,
                    name = cleanName,
                    email = user.email,
                    phone = cleanPhone,
                    role = user.role.code
                )

                val response = RetrofitClient.apiService.updateUser(user.id, userDto)
                if (response.isSuccessful) {
                    val updatedUser = user.copy(
                        name = cleanName,
                        phone = cleanPhone
                    )
                    UserSession.currentUser = updatedUser
                    _uiState.update { it.copy(isLoading = false, isUpdated = true) }
                    onSuccess()
                } else {
                    val err = response.errorBody()?.string()
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = if (!err.isNullOrBlank()) err else "Грешка при запазване на профила"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Грешка: ${e.message}") }
            }
        }
    }

    fun deleteProfile(onSuccess: () -> Unit) {
        val user = UserSession.currentUser ?: return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteUserByEmail(user.email)
                if (response.isSuccessful) {
                    UserSession.logout()
                    _uiState.update { it.copy(isLoading = false, isDeleted = true) }
                    onSuccess()
                } else {
                    val err = response.errorBody()?.string()
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            error = if (!err.isNullOrBlank()) err else "Грешка при изтриване на профила"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Грешка: ${e.message}") }
            }
        }
    }
}
