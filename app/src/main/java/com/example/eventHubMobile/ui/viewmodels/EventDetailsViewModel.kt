package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.Event
import com.example.eventHubMobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventDetailsUiState(
    val event: Event? = null,
    val isLoading: Boolean = false,
    val isBooking: Boolean = false,
    val isBooked: Boolean = false,
    val error: String? = null
)

class EventDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: Long) {
        val cachedEvent = UserSession.events.find { it.id == eventId }
        val alreadyBooked = UserSession.myTickets.any { it.id == eventId }
        _uiState.update { it.copy(event = cachedEvent, isBooked = alreadyBooked) }
    }

    fun bookEvent(eventId: Long) {
        val userId = UserSession.currentUser?.id?.toLong() ?: return
        
        _uiState.update { it.copy(isBooking = true, error = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.bookEvent(userId, eventId)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isBooking = false, isBooked = true) }

                    _uiState.value.event?.let { 
                        if (!UserSession.myTickets.any { t -> t.id == it.id }) {
                            UserSession.myTickets.add(it)
                        }
                    }
                } else {
                    _uiState.update { it.copy(isBooking = false, error = "Грешка при записване") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isBooking = false, error = e.message) }
            }
        }
    }
}
