package com.example.eventHubMobile.network

data class EventLocationDto(
    val lat: Double?,
    val lng: Double?,
    val city: String?,
    val countryCode: String?,
    val address: String?
)

data class EventDto(
    val id: Long,
    val name: String,
    val type: String,
    val status: String,
    val description: String?,
    val ticketPrice: Double,
    val currency: String,
    val availableTickets: Int,
    val totalTickets: Int,
    val ticketsSold: Int,
    val startDateTime: String,
    val endDateTime: String,
    val coverImageUrl: String?,
    val roomName: String?,
    val location: EventLocationDto?
)

data class CreateEventRequest(
    val name: String,
    val type: String,
    val status: String = "Active",
    val description: String?,
    val ticketPrice: Double,
    val currency: String = "EUR",
    val totalTickets: Int,
    val availableTickets: Int,
    val ticketsSold: Int = 0,
    val startDateTime: String,
    val endDateTime: String,
    val roomName: String?,
    val location: EventLocationDto?
)

data class UserDto(
    val id: Long? = null,
    val name: String,
    val email: String,
    val password: String? = null,
    val role: String? = null,
    val phone: String? = null,
    val createdAt: String? = null,
    val tickets: List<TicketDto>? = emptyList()
)

data class TicketDto(
    val id: Long,
    val event: EventDto,
    val purchaseDate: String?,
    val status: String?
)

data class NotificationDto(
    val id: Long,
    val title: String,
    val message: String,
    val createdAt: String?
)

data class MessageDto(
    val id: Long,
    val senderId: Long,
    val receiverId: Long,
    val text: String,
    val sentAt: String
)

data class ChatSummaryDto(
    val otherUserId: Long,
    val otherUserName: String,
    val lastMessage: String,
    val lastMessageTime: String
)

data class SendMessageRequest(
    val senderId: Long,
    val receiverId: Long,
    val text: String
)

data class LoginRequest(
    val email: String,
    val password: String
)
