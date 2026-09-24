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

data class MyTicketsUiState(
    val tickets: List<Event> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class MyTicketsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyTicketsUiState())
    val uiState: StateFlow<MyTicketsUiState> = _uiState.asStateFlow()

    init {
        loadMyTickets()
    }

    fun loadMyTickets() {
        val userId = UserSession.currentUser?.id?.toLong() ?: return
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyEvents(userId)
                if (response.isSuccessful && response.body() != null) {
                    val mappedEvents = response.body()!!.map { ticketDto -> 
                        ticketDto.event.toDomain() 
                    }
                    

                    UserSession.myTickets.clear()
                    UserSession.myTickets.addAll(mappedEvents)
                    
                    _uiState.update { it.copy(tickets = mappedEvents, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Грешка при зареждане") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
