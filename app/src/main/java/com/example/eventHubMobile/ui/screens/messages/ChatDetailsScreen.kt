package com.example.eventHubMobile.ui.screens.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventHubMobile.R
import com.example.eventHubMobile.ui.viewmodels.ChatViewModel

@Composable
fun ChatDetailsScreen(
    otherUserId: Long,
    userName: String?,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var textInput by remember { mutableStateOf("") }

    LaunchedEffect(otherUserId) {
        viewModel.loadHistory(otherUserId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            Text(
                text = userName ?: "Чат",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages) { msg ->
                val alignment = if (msg.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
                val bubbleColor = if (msg.isFromMe) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
                val textColor = if (msg.isFromMe) Color.White else Color.Black

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
                    Surface(
                        color = bubbleColor,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isFromMe) 16.dp else 0.dp,
                            bottomEnd = if (msg.isFromMe) 0.dp else 16.dp
                        )
                    ) {
                        Text(
                            text = msg.text,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.type_message)) },
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendMessage(otherUserId, textInput)
                        textInput = ""
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}
