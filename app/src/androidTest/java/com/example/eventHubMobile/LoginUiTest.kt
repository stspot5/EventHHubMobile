package com.example.eventHubMobile

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.compose.rememberNavController
import com.example.eventHubMobile.ui.screens.auth.LoginScreen
import com.example.eventHubMobile.ui.theme.EventHHubMobileTheme
import org.junit.Rule
import org.junit.Test

class LoginUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displays_hub_text() {
        composeTestRule.setContent {
            EventHHubMobileTheme {

                LoginScreen(navController = rememberNavController())
            }
        }


        composeTestRule.onNodeWithText("Event").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hub").assertIsDisplayed()
    }
}
