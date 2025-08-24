package com.example.tailorsapprentice.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tailorsapprentice.data.Measurement
import com.example.tailorsapprentice.repository.TailorRepository
import com.example.tailorsapprentice.ui.components.ConfirmDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditMeasurementScreen(
    repo: TailorRepository,
    customerId: Long,
    measurementId: Long,
    onDone: () -> Unit
) {
    val measurementState = remember { mutableStateOf<Measurement?>(null) }
    val loadingState = remember { mutableStateOf(true) }

    LaunchedEffect(customerId, measurementId) {
        launch {
            repo.measurementsFor(customerId).collectLatest { list ->
                measurementState.value = list.firstOrNull { it.id == measurementId }
                loadingState.value = false
            }
        }
    }

    val m = measurementState.value

    var chest by remember { mutableStateOf(m?.chest?.toString() ?: "") }
    var waist by remember { mutableStateOf(m?.waist?.toString() ?: "") }
    var hips by remember { mutableStateOf(m?.hips?.toString() ?: "") }
    var sleeve by remember { mutableStateOf(m?.sleeve?.toString() ?: "") }
    var shoulder by remember { mutableStateOf(m?.shoulder?.toString() ?: "") }
    var neck by remember { mutableStateOf(m?.neck?.toString() ?: "") }
    var notes by remember { mutableStateOf(m?.notes ?: "") }

    // keep fields in sync when measurement loads
    LaunchedEffect(m) {
        if (m != null) {
            chest = m.chest?.toString() ?: ""
            waist = m.waist?.toString() ?: ""
            hips = m.hips?.toString() ?: ""
            sleeve = m.sleeve?.toString() ?: ""
            shoulder = m.shoulder?.toString() ?: ""
            neck = m.neck?.toString() ?: ""
            notes = m.notes ?: ""
        }
    }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun parseFloatOrNull(s: String): Float? {
        val cleaned = s.trim().replace(',', '.')
        return cleaned.toFloatOrNull()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Edit Measurement") })
    }) { padding ->
        if (loadingState.value) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Text("Loading...")
            }
            return@Scaffold
        }

        if (m == null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)) {
                Text("Measurement not found.")
            }
            return@Scaffold
        }

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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(modifier = Modifier.weight(1f), onClick = {
                    // validation: require at least one numeric measurement
                    val values = listOf(chest, waist, hips, sleeve, shoulder, neck)
                    val parsed = values.map { parseFloatOrNull(it) }
                    val anyEntered = parsed.any { it != null }
                    if (!anyEntered) {
                        errorMessage = "Please enter at least one measurement value."
                        return@Button
                    }
                    // check positive numbers if present
                    val negative = parsed.filterNotNull().any { it <= 0f }
                    if (negative) {
                        errorMessage = "Measurements must be positive numbers."
                        return@Button
                    }
                    errorMessage = null
                    val updated = m.copy(
                        chest = parseFloatOrNull(chest),
                        waist = parseFloatOrNull(waist),
                        hips = parseFloatOrNull(hips),
                        sleeve = parseFloatOrNull(sleeve),
                        shoulder = parseFloatOrNull(shoulder),
                        neck = parseFloatOrNull(neck),
                        notes = notes.ifBlank { null }
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        repo.updateMeasurement(updated)
                        onDone()
                    }
                }) {
                    Text("Save")
                }

                OutlinedButton(modifier = Modifier.weight(1f), onClick = {
                    showDeleteConfirm = true
                }) {
                    Text("Delete")
                }
            }

            if (showDeleteConfirm) {
                ConfirmDialog(
                    title = "Delete Measurement",
                    message = "Are you sure you want to delete this measurement? This action cannot be undone.",
                    confirmText = "Delete",
                    dismissText = "Cancel",
                    onConfirm = {
                        showDeleteConfirm = false
                        CoroutineScope(Dispatchers.IO).launch {
                            repo.deleteMeasurement(m)
                            onDone()
                        }
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }
    }
}