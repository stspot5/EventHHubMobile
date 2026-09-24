package com.example.eventHubMobile.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eventHubMobile.R
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.UserRole
import com.example.eventHubMobile.navigation.Screen
import com.example.eventHubMobile.ui.viewmodels.UserListViewModel

@Composable
fun UserListScreen(
    navController: NavController,
    viewModel: UserListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = UserSession.currentUser
    var searchQuery by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    val filteredUsers = remember(searchQuery, uiState.users) {
        if (searchQuery.isEmpty()) {
            uiState.users
        } else {
            uiState.users.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.search_users)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.error!!, color = Color.Red)
                    Button(onClick = { viewModel.loadUsers() }) {
                        Text("Опитай пак")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredUsers) { user ->
                    val isCurrentLoggedInOrganizer = currentUser?.role == UserRole.ORGANIZER &&
                            ((currentUser.id != 0L && user.id == currentUser.id) || user.email.equals(currentUser.email, ignoreCase = true))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = if(user.role == UserRole.ORGANIZER) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text(
                                        text = if(user.role == UserRole.ORGANIZER) "Организатор" else "Участник",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if(user.role == UserRole.ORGANIZER) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            if (isCurrentLoggedInOrganizer) {
                                IconButton(onClick = {
                                    navController.navigate(Screen.EditProfile.route)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit User",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
