package com.example.eventHubMobile.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventHubMobile.R
import com.example.eventHubMobile.ui.viewmodels.EventDetailsViewModel

@Composable
fun EventDetailsScreen(
    eventId: String?,
    viewModel: EventDetailsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val idLong = eventId?.toLongOrNull() ?: 0L

    LaunchedEffect(idLong) {
        viewModel.loadEvent(idLong)
    }

    val event = uiState.event ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = event.category.uppercase(),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(24.dp)
                .offset(y = (-30).dp)
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            InfoRow(icon = Icons.Default.CalendarToday, text = event.date)
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(icon = Icons.Default.LocationOn, text = "${event.city}, ${event.locationName}")
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(icon = Icons.Default.ConfirmationNumber, text = "Налични билети: ${event.availableTickets}")

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Относно събитието",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 26.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Обща цена", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Text(
                            text = if (event.price > 0) "${event.price} EUR" else "БЕЗПЛАТНО",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    Button(
                        onClick = { viewModel.bookEvent(event.id) },
                        enabled = !uiState.isBooked && !uiState.isBooking && event.availableTickets > 0,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Gray
                        )
                    ) {
                        if (uiState.isBooking) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = when {
                                    uiState.isBooked -> "ЗАПИСАН"
                                    event.availableTickets <= 0 -> "ИЗЧЕРПАНИ"
                                    else -> stringResource(R.string.book_now).uppercase()
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            uiState.error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
