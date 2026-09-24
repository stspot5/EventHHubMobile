package com.example.eventHubMobile.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.eventHubMobile.R
import com.example.eventHubMobile.data.local.UserSession
import com.example.eventHubMobile.data.model.UserRole
import com.example.eventHubMobile.ui.screens.auth.LoginScreen
import com.example.eventHubMobile.ui.screens.auth.RegisterScreen
import com.example.eventHubMobile.ui.screens.events.*
import com.example.eventHubMobile.ui.screens.profile.*
import com.example.eventHubMobile.ui.screens.messages.*
import com.example.eventHubMobile.ui.screens.notifications.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isLoggedIn = UserSession.isLoggedIn
    val currentUser = UserSession.currentUser

    val drawerMenuItems = remember(isLoggedIn, currentUser) {
        val list = mutableListOf<Screen>(Screen.Home)
        if (isLoggedIn) {
            list.add(Screen.MyTickets)
            list.add(Screen.Messages)
            list.add(Screen.Notifications)
            
            if (currentUser?.role == UserRole.ORGANIZER) {
                list.add(Screen.AddEvent)
                list.add(Screen.UserList)
            }
            list.add(Screen.Settings)
        }
        list
    }

    val allScreens = listOf(
        Screen.Home, Screen.Login, Screen.Register, Screen.EditProfile,
        Screen.UserList, Screen.EventDetails, Screen.AddEvent,
        Screen.MyTickets, Screen.Messages, Screen.ChatDetails,
        Screen.Notifications, Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isTopLevelDestination = drawerMenuItems.any { it.route == currentRoute } || currentRoute == Screen.Home.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isLoggedIn,
        drawerContent = {
            if (isLoggedIn) {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White
                ) {
                    // Header на Drawer-а с градиент
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                )
                            ),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Surface(
                                shape = RoundedCornerShape(30.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(60.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = currentUser?.name ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if(currentUser?.role == UserRole.ORGANIZER) "Организатор" else "Участник",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    drawerMenuItems.forEach { screen ->
                        NavigationDrawerItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(stringResource(screen.titleRes), fontWeight = FontWeight.Medium) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = Color.Gray
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        label = { Text("Изход", fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            UserSession.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.Red, unselectedIconColor = Color.Red),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 24.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (isLoggedIn || currentRoute == Screen.Login.route || currentRoute == Screen.Register.route) {
                    CenterAlignedTopAppBar(
                        title = {
                            val currentScreen = allScreens.find { it.route == currentRoute }
                            Text(
                                text = stringResource(currentScreen?.titleRes ?: R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isTopLevelDestination) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        navigationIcon = {
                            if (isLoggedIn && isTopLevelDestination) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            } else if (currentRoute != Screen.Home.route && currentRoute != Screen.Login.route) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.White
                        )
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen(navController) }
                composable(Screen.Login.route) { LoginScreen(navController) }
                composable(Screen.Register.route) { RegisterScreen(navController) }
                composable(Screen.Settings.route) { SettingsScreen() }
                composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
                composable(Screen.UserList.route) { UserListScreen(navController) }
                composable(
                    route = Screen.EventDetails.route,
                    arguments = listOf(androidx.navigation.navArgument("eventId") { type = androidx.navigation.NavType.StringType })
                ) { backStackEntry ->
                    EventDetailsScreen(backStackEntry.arguments?.getString("eventId"))
                }
                composable(Screen.AddEvent.route) { 
                    AddEventScreen(onEventCreated = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) 
                }
                composable(Screen.MyTickets.route) { MyTicketsScreen() }
                composable(Screen.Messages.route) { MessagesScreen(navController) }
                composable(
                    route = Screen.ChatDetails.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("otherUserId") { type = androidx.navigation.NavType.LongType },
                        androidx.navigation.navArgument("userName") { type = androidx.navigation.NavType.StringType }
                    )
                ) { backStackEntry ->
                    ChatDetailsScreen(
                        otherUserId = backStackEntry.arguments?.getLong("otherUserId") ?: 0L,
                        userName = backStackEntry.arguments?.getString("userName")
                    )
                }
                composable(Screen.Notifications.route) { NotificationsScreen() }
            }
        }
    }
}
