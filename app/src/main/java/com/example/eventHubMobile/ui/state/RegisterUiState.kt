package com.example.eventHubMobile.ui.state

import androidx.annotation.StringRes
import com.example.eventHubMobile.data.model.UserRole

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: UserRole = UserRole.PARTICIPANT,
    
    @StringRes val usernameError: Int? = null,
    @StringRes val emailError: Int? = null,
    @StringRes val passwordError: Int? = null,
    @StringRes val confirmPasswordError: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
