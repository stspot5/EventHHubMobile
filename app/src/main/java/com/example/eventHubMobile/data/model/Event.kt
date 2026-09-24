package com.example.eventHubMobile.data.model

data class Event(
    val id: Long,
    val title: String,
    val description: String,
    val date: String,
    val locationName: String,
    val city: String,
    val category: String,
    val price: Double,
    val currency: String,
    val availableTickets: Int,
    val imageUrl: String? = null
)
