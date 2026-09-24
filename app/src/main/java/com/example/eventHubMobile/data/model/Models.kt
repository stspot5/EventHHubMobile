package com.example.eventHubMobile.data.model

data class User(
    val id: Long = 0L,
    val name: String,
    val email: String,
    val phone: String? = null,
    val password: String = "123456",
    val role: UserRole = UserRole.PARTICIPANT
)

data class ChatSummary(
    val id: Int,
    val userName: String,
    val lastMessage: String,
    val time: String
)

data class Message(
    val id: Int,
    val text: String,
    val isFromMe: Boolean
)

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val date: String
)
