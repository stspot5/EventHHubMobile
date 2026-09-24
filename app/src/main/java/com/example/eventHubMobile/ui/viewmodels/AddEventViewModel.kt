package com.example.eventHubMobile.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventHubMobile.R
import com.example.eventHubMobile.data.SecurityUtils
import com.example.eventHubMobile.network.CreateEventRequest
import com.example.eventHubMobile.network.EventLocationDto
import com.example.eventHubMobile.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEventUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    

    val nameError: Int? = null,
    val descriptionError: Int? = null,
    val dateError: Int? = null,
    val cityError: Int? = null,
    val addressError: Int? = null,
    val priceError: Int? = null,
    val ticketsError: Int? = null
)

class AddEventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddEventUiState())
    val uiState: StateFlow<AddEventUiState> = _uiState.asStateFlow()

    fun createEvent(
        name: String,
        description: String,
        startDate: String,
        endDate: String,
        address: String,
        city: String,
        category: String,
        price: Double,
        tickets: Int,
        onSuccess: () -> Unit
    ) {

        val nameErr = if (name.isBlank() || !SecurityUtils.isSafe(name)) R.string.err_invalid_input else null
        val descErr = if (description.length > 1000) R.string.err_field_too_long else null
        val dateErr = if (startDate.isBlank() || endDate.isBlank()) R.string.err_date_required else null
        val cityErr = if (city.isBlank() || !SecurityUtils.isSafe(city)) R.string.err_invalid_input else null
        val addrErr = if (address.isBlank() || !SecurityUtils.isSafe(address)) R.string.err_invalid_input else null
        val priceErr = if (price < 0) R.string.err_price_invalid else null
        val tickErr = if (tickets <= 0) R.string.err_tickets_invalid else null

        if (listOf(nameErr, descErr, dateErr, cityErr, addrErr, priceErr, tickErr).any { it != null }) {
            _uiState.update { it.copy(
                nameError = nameErr,
                descriptionError = descErr,
                dateError = dateErr,
                cityError = cityErr,
                addressError = addrErr,
                priceError = priceErr,
                ticketsError = tickErr
            ) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {

                val request = CreateEventRequest(
                    name = SecurityUtils.sanitize(name),
                    type = category,
                    description = SecurityUtils.sanitize(description),
                    ticketPrice = price,
                    totalTickets = tickets,
                    availableTickets = tickets,
                    ticketsSold = 0,
                    startDateTime = startDate,
                    endDateTime = endDate,
                    roomName = null,
                    currency = "EUR",
                    location = EventLocationDto(
                        lat = 0.0,
                        lng = 0.0,
                        city = SecurityUtils.sanitize(city),
                        countryCode = "BG",
                        address = SecurityUtils.sanitize(address)
                    )
                )

                val response = RetrofitClient.apiService.createEvent(request)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Сървърна грешка: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
