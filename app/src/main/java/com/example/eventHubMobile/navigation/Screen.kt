package com.example.eventHubMobile.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.eventHubMobile.R

sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Home : Screen(route = "home", titleRes = R.string.home_title, icon = Icons.Default.Home)
    object Login : Screen(route = "login", titleRes = R.string.login_title, icon = Icons.Default.Lock)
    object Register : Screen(route = "register", titleRes = R.string.register_title, icon = Icons.Default.PersonAdd)
    object Settings : Screen(route = "settings", titleRes = R.string.settings_title, icon = Icons.Default.Settings)
    object EditProfile : Screen(route = "edit_profile", titleRes = R.string.edit_profile_title, icon = Icons.Default.Person)
    object UserList : Screen(route = "user_list", titleRes = R.string.user_list_title, icon = Icons.AutoMirrored.Filled.List)
    object EventDetails : Screen(route = "event_details/{eventId}", titleRes = R.string.event_details, icon = Icons.Default.Home)
    object AddEvent : Screen(route = "add_event", titleRes = R.string.add_event_title, icon = Icons.Default.Add)
    object MyTickets : Screen(route = "my_tickets", titleRes = R.string.my_tickets_title, icon = Icons.Default.ConfirmationNumber)
    object Messages : Screen(route = "messages", titleRes = R.string.messages_title, icon = Icons.AutoMirrored.Filled.Chat)
    object ChatDetails : Screen(route = "chat_details/{otherUserId}/{userName}", titleRes = R.string.messages_title, icon = Icons.AutoMirrored.Filled.Chat)
    object Notifications : Screen(route = "notifications", titleRes = R.string.notifications_title, icon = Icons.Default.Notifications)
}
