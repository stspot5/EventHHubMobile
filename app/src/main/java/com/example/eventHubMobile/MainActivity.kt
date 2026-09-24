package com.example.eventHubMobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.eventHubMobile.navigation.MainAppScreen
import com.example.eventHubMobile.ui.theme.EventHHubMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EventHHubMobileTheme {
                MainAppScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    EventHHubMobileTheme {
        MainAppScreen()
    }
}
