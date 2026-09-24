package com.example.eventHubMobile.ui.state

import androidx.annotation.StringRes

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    
    @StringRes val usernameError: Int? = null,
    @StringRes val passwordError: Int? = null,
    
    val isLoading: Boolean = false,
    val loginError: String? = null
)
