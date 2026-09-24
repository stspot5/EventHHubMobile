package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.R
import com.example.eventHubMobile.data.SecurityUtils
import com.example.eventHubMobile.data.model.UserRole
import com.example.eventHubMobile.network.RetrofitClient
import com.example.eventHubMobile.network.UserDto
import com.example.eventHubMobile.ui.state.RegisterUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, usernameError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onRoleSelected(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val state = _uiState.value


        val cleanUsername = SecurityUtils.sanitize(state.username)
        val cleanEmail = SecurityUtils.sanitize(state.email)


        val usernameErr = when {
            state.username.isBlank() -> R.string.err_username_required
            !SecurityUtils.isSafe(state.username) -> R.string.err_invalid_input
            state.username.length > 50 -> R.string.err_field_too_long
            else -> null
        }


        val emailErr = when {
            state.email.isBlank() -> R.string.err_email_required
            !SecurityUtils.isValidEmail(state.email) -> R.string.err_email_invalid
            !SecurityUtils.isSafe(state.email) -> R.string.err_invalid_input
            else -> null
        }


        val passwordErr = when {
            state.password.isBlank() -> R.string.err_password_required
            state.password.length < 6 -> R.string.err_password_short
            state.password.length > 100 -> R.string.err_field_too_long
            else -> null
        }


        val confirmPasswordErr = when {
            state.confirmPassword.isBlank() -> R.string.err_confirm_password
            state.confirmPassword != state.password -> R.string.err_passwords_dont_match
            else -> null
        }

        val hasErrorList = listOf(usernameErr, emailErr, passwordErr, confirmPasswordErr)
        val hasError = hasErrorList.any { it != null }

        if (hasError) {
            _uiState.update {
                it.copy(
                    usernameError = usernameErr,
                    emailError = emailErr,
                    passwordError = passwordErr,
                    confirmPasswordError = confirmPasswordErr,
                    errorMessage = null
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            viewModelScope.launch {
                try {
                    val userDto = UserDto(
                        name = cleanUsername,
                        email = cleanEmail,
                        password = state.password,
                        role = state.selectedRole.code,
                        phone = null,
                        createdAt = null
                    )
                    
                    val response = RetrofitClient.apiService.register(userDto)
                    
                    if (response.isSuccessful) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                        onSuccess()
                    } else {
                        val errorText = response.errorBody()?.string()
                        val msg = if (!errorText.isNullOrBlank()) errorText else "Грешка при регистрация. Опитайте отново."
                        _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Грешка при връзка със сървъра: ${e.message}") }
                }
            }
        }
    }
}
