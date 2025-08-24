package com.example.tailorsapprentice.ui.screens.customer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddMeasurementScreen(
    onAdd: (name: String, value: Double) -> Unit = { _, _ -> },
    onBack: () -> Unit = {}
) {
    val nameState = remember { mutableStateOf("") }
    val valueState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Add Measurement")
        Spacer(Modifier.height(8.dp))

        TextField(
            value = nameState.value,
            onValueChange = { nameState.value = it },
            label = { Text("Name") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = valueState.value,
            onValueChange = { valueState.value = it },
            label = { Text("Value") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (errorState.value != null) {
            Text(text = errorState.value!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        Button(onClick = {
            val valueDouble = valueState.value.toDoubleOrNull()
            when {
                nameState.value.isBlank() -> errorState.value = "Name is required"
                valueDouble == null -> errorState.value = "Enter a valid number"
                valueDouble <= 0.0 -> errorState.value = "Value must be greater than zero"
                else -> {
                    errorState.value = null
                    onAdd(nameState.value.trim(), valueDouble)
                }
            }
        }) { Text("Add") }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBack) { Text("Back") }
    }
}
