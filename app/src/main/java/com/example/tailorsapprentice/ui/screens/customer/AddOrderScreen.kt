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
fun AddOrderScreen(
    onCreateOrder: (quantity: Int, price: Double) -> Unit = { _, _ -> },
    onBack: () -> Unit = {}
) {
    val quantityState = remember { mutableStateOf("") }
    val priceState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Create Order")
        Spacer(Modifier.height(8.dp))

        TextField(
            value = quantityState.value,
            onValueChange = { quantityState.value = it },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = priceState.value,
            onValueChange = { priceState.value = it },
            label = { Text("Price") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (errorState.value != null) {
            Text(text = errorState.value!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        Button(onClick = {
            val quantityInt = quantityState.value.toIntOrNull()
            val priceDouble = priceState.value.toDoubleOrNull()
            when {
                quantityInt == null || quantityInt <= 0 -> errorState.value = "Enter a valid quantity"
                priceDouble == null || priceDouble <= 0.0 -> errorState.value = "Enter a valid price"
                else -> {
                    errorState.value = null
                    onCreateOrder(quantityInt, priceDouble)
                }
            }
        }) { Text("Create") }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onBack) { Text("Back") }
    }
}
