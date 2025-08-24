package com.example.tailorsapprentice.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.*;
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tailorsapprentice.data.Measurement
import com.example.tailorsapprentice.repository.TailorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddMeasurementScreen(repo: TailorRepository, customerId: Long, onDone: () -> Unit) {
    var chest by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var hips by remember { mutableStateOf("") }
    var sleeve by remember { mutableStateOf("") }
    var shoulder by remember { mutableStateOf("") }
    var neck by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun parseFloatOrNull(s: String): Float? {
        val cleaned = s.trim().replace(',', '.')
        return cleaned.toFloatOrNull()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Add Measurement") })
    }) { padding ->
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()) {

            OutlinedTextField(
                value = chest,
                onValueChange = { chest = it },
                label = { Text("Chest (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = waist,
                onValueChange = { waist = it },
                label = { Text("Waist (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = hips,
                onValueChange = { hips = it },
                label = { Text("Hips (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = sleeve,
                onValueChange = { sleeve = it },
                label = { Text("Sleeve (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = shoulder,
                onValueChange = { shoulder = it },
                label = { Text("Shoulder (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = neck,
                onValueChange = { neck = it },
                label = { Text("Neck (cm)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = MaterialTheme.colors.error)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                // Validation: require at least one numeric field and positive numbers
                val values = listOf(chest, waist, hips, sleeve, shoulder, neck)
                val parsed = values.map { parseFloatOrNull(it) }
                val anyEntered = parsed.any { it != null }
                if (!anyEntered) {
                    errorMessage = "Please enter at least one measurement."
                    return@Button
                }
                val negative = parsed.filterNotNull().any { it <= 0f }
                if (negative) {
                    errorMessage = "Measurements must be positive numbers."
                    return@Button
                }
                errorMessage = null

                val measurement = Measurement(
                    customerId = customerId,
                    chest = parseFloatOrNull(chest),
                    waist = parseFloatOrNull(waist),
                    hips = parseFloatOrNull(hips),
                    sleeve = parseFloatOrNull(sleeve),
                    shoulder = parseFloatOrNull(shoulder),
                    neck = parseFloatOrNull(neck),
                    notes = notes.ifBlank { null }
                )

                CoroutineScope(Dispatchers.IO).launch {
                    repo.insertMeasurement(measurement)
                    onDone()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Measurement")
            }
        }
    }
}