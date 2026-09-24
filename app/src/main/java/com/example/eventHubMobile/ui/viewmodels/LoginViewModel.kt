package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.R
import com.example.eventHubMobile.data.SecurityUtils
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.User
import com.example.eventHubMobile.data.model.UserRole
import com.example.eventHubMobile.network.LoginRequest
import com.example.eventHubMobile.network.RetrofitClient
import com.example.eventHubMobile.ui.state.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, usernameError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.update { it.copy(rememberMe = value) }
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val state = _uiState.value

        
        val usernameErr = when {
            state.username.isBlank() -> R.string.err_username_required
            !SecurityUtils.isValidEmail(state.username) -> R.string.err_email_invalid
            !SecurityUtils.isSafe(state.username) -> R.string.err_invalid_input
            else -> null
        }
        
        val passwordErr = if (state.password.isBlank()) R.string.err_password_required else null

        if (usernameErr != null || passwordErr != null) {
            _uiState.update { 
                it.copy(usernameError = usernameErr, passwordError = passwordErr) 
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, loginError = null) }

        viewModelScope.launch {
            try {
                
                val cleanEmail = SecurityUtils.sanitize(state.username)
                
                val response = RetrofitClient.apiService.login(LoginRequest(cleanEmail, state.password))
                
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    
                    val id = (body["id"] as? Double)?.toLong() ?: 0L
                    val name = body["name"] as? String ?: "Потребител"
                    val email = body["email"] as? String ?: state.username
                    val phone = body["phone"] as? String
                    val roleStr = body["role"] as? String ?: "participant"
                    
                    val role = UserRole.fromCode(roleStr)
                    
                    UserSession.currentUser = User(id = id, name = name, email = email, phone = phone, role = role)
                    UserSession.isLoggedIn = true
                    
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            loginError = "Грешен имейл или парола"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        loginError = "Грешка при връзка със сървъра: ${e.message}"
                    ) 
                }
            }
        }
    }
}
