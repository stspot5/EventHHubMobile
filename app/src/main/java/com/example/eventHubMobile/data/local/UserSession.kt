package com.example.eventHubMobile.data.local

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.eventHubMobile.data.model.Event
import com.example.eventHubMobile.data.model.NotificationItem
import com.example.eventHubMobile.data.model.User

object UserSession {

    var isLoggedIn by mutableStateOf(false)
    var currentUser: User? by mutableStateOf(null)
    val events = mutableStateListOf<Event>()
    val myTickets = mutableStateListOf<Event>()
    val notifications = mutableStateListOf<NotificationItem>()

    fun logout() {
        currentUser = null
        isLoggedIn = false
        events.clear()
        myTickets.clear()
        notifications.clear()
    }
}
