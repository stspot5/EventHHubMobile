package com.example.eventHubMobile.ui.screens.events

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventHubMobile.data.model.EventCategory
import com.example.eventHubMobile.ui.viewmodels.AddEventViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    onEventCreated: () -> Unit,
    viewModel: AddEventViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EventCategory.CONFERENCE) }
    var price by remember { mutableStateOf("") }
    var tickets by remember { mutableStateOf("100") }

    var categoryMenuExpanded by remember { mutableStateOf(false) }

    fun showDateTimePicker(onDateTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val formatted = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02dT%02d:%02d:00",
                            year, month + 1, dayOfMonth, hourOfDay, minute
                        )
                        onDateTimeSelected(formatted)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Име на събитието") },
            isError = uiState.nameError != null,
            supportingText = { uiState.nameError?.let { Text(context.getString(it)) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = categoryMenuExpanded,
            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Категория") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            ExposedDropdownMenu(
                expanded = categoryMenuExpanded,
                onDismissRequest = { categoryMenuExpanded = false }
            ) {
                EventCategory.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            selectedCategory = category
                            categoryMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = startDate.replace("T", " "),
                onValueChange = {},
                readOnly = true,
                label = { Text("Начало") },
                isError = uiState.dateError != null,
                leadingIcon = { 
                    IconButton(onClick = { showDateTimePicker { startDate = it } }) {
                        Icon(Icons.Default.CalendarToday, null)
                    }
                },
                modifier = Modifier.weight(1f).clickable { showDateTimePicker { startDate = it } },
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = endDate.replace("T", " "),
                onValueChange = {},
                readOnly = true,
                label = { Text("Край") },
                isError = uiState.dateError != null,
                leadingIcon = { 
                    IconButton(onClick = { showDateTimePicker { endDate = it } }) {
                        Icon(Icons.Default.Schedule, null)
                    }
                },
                modifier = Modifier.weight(1f).clickable { showDateTimePicker { endDate = it } },
                shape = RoundedCornerShape(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Град") },
            isError = uiState.cityError != null,
            supportingText = { uiState.cityError?.let { Text(context.getString(it)) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Адрес / Място") },
            isError = uiState.addressError != null,
            supportingText = { uiState.addressError?.let { Text(context.getString(it)) } },
            modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
            shape = RoundedCornerShape(16.dp),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Цена (EUR)") },
                isError = uiState.priceError != null,
                supportingText = { uiState.priceError?.let { Text(context.getString(it)) } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = tickets,
                onValueChange = { tickets = it },
                label = { Text("Брой билети") },
                isError = uiState.ticketsError != null,
                supportingText = { uiState.ticketsError?.let { Text(context.getString(it)) } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") },
            isError = uiState.descriptionError != null,
            supportingText = { uiState.descriptionError?.let { Text(context.getString(it)) } },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(16.dp)
        )

        if (uiState.error != null) {
            Text(text = uiState.error!!, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.createEvent(
                    name = name,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    city = city,
                    address = address,
                    category = selectedCategory.name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    tickets = tickets.toIntOrNull() ?: 0,
                    onSuccess = onEventCreated
                )
            },
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("ПУБЛИКУВАЙ СЪБИТИЕ", fontWeight = FontWeight.Bold)
            }
        }
    }
}
