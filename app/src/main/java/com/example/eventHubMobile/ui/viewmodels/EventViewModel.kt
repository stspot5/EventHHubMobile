package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.Event
import com.example.eventHubMobile.network.EventDto
import com.example.eventHubMobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EventUiState(
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class EventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllEvents()
                if (response.isSuccessful && response.body() != null) {
                    val mappedEvents = response.body()!!.map { dto ->
                        dto.toDomain()
                    }
                    UserSession.events.clear()
                    UserSession.events.addAll(mappedEvents)
                    
                    _uiState.update { it.copy(events = mappedEvents, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Грешка при зареждане: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

fun EventDto.toDomain(): Event {
    return Event(
        id = this.id,
        title = this.name, 
        description = this.description ?: "",
        date = this.startDateTime.replace("T", " "), 
        locationName = this.location?.address ?: this.roomName ?: "Unknown Location",
        city = this.location?.city ?: "",
        category = this.type, 
        price = this.ticketPrice, 
        currency = this.currency,
        availableTickets = this.availableTickets,
        imageUrl = this.coverImageUrl
    )
}
