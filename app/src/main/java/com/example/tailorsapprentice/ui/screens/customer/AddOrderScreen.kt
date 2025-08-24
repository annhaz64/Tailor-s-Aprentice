package com.example.tailorsapprentice.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material.*;
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.tailorsapprentice.data.OrderEntity
import com.example.tailorsapprentice.repository.TailorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddOrderScreen(repo: TailorRepository, customerId: Long, onDone: () -> Unit) {
    var description by remember { mutableStateOf("") }
    var expectedDelivery by remember { mutableStateOf("") } // expected format: yyyy-MM-dd
    var status by remember { mutableStateOf("pending") } // pending, in_progress, ready, delivered
    var totalAmountText by remember { mutableStateOf("") }
    var amountPaidText by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // live computed values
    val totalAmount = totalAmountText.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
    val amountPaid = amountPaidText.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
    val balance = (totalAmount - amountPaid).coerceAtLeast(0.0)
    val paidInFull = balance <= 0.0

    fun parseDateToEpoch(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            fmt.parse(dateStr.trim())?.time
        } catch (e: Exception) {
            null
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Add Order") })
    }) { padding ->
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()) {

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Order description (e.g., 2 shirts, 1 pair of pants)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = expectedDelivery,
                onValueChange = { expectedDelivery = it },
                label = { Text("Expected delivery date (yyyy-MM-dd, optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Status")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("pending", "in_progress", "ready", "delivered").forEach { s ->
                    OutlinedButton(
                        onClick = { status = s },
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (status == s) MaterialTheme.colors.primary.copy(alpha = 0.12f) else MaterialTheme.colors.surface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(s.replace('_', ' ').replaceFirstChar { it.uppercaseChar() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = totalAmountText,
                onValueChange = { totalAmountText = it },
                label = { Text("Total amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = amountPaidText,
                onValueChange = { amountPaidText = it },
                label = { Text("Amount paid") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Balance: %.2f".format(balance))
            Text(
                if (paidInFull) "Paid in full" else "Still owes money: %.2f".format(balance),
                color = if (paidInFull) MaterialTheme.colors.primary else MaterialTheme.colors.error
            )

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = MaterialTheme.colors.error)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                // Basic validation
                if (description.isBlank()) {
                    errorMessage = "Please add a short description for the order."
                    return@Button
                }
                if (totalAmountText.isBlank()) {
                    errorMessage = "Please enter the total amount."
                    return@Button
                }
                val total = totalAmountText.trim().replace(',', '.').toDoubleOrNull()
                val paid = amountPaidText.trim().replace(',', '.').toDoubleOrNull() ?: 0.0
                if (total == null || total < 0.0) {
                    errorMessage = "Total amount must be a valid non-negative number."
                    return@Button
                }
                if (paid < 0.0) {
                    errorMessage = "Amount paid must be a non-negative number."
                    return@Button
                }
                if (paid > total) {
                    errorMessage = "Amount paid cannot be greater than total amount."
                    return@Button
                }
                val expectedEpoch = parseDateToEpoch(expectedDelivery)
                if (!expectedDelivery.isNullOrBlank() && expectedEpoch == null) {
                    errorMessage = "Expected delivery date must be in yyyy-MM-dd format."
                    return@Button
                }
                errorMessage = null

                val order = OrderEntity(
                    customerId = customerId,
                    description = description.trim(),
                    orderDate = System.currentTimeMillis(),
                    expectedDeliveryDate = expectedEpoch,
                    status = status,
                    totalAmount = total,
                    amountPaid = paid
                )

                CoroutineScope(Dispatchers.IO).launch {
                    repo.insertOrder(order)
                    onDone()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Order")
            }
        }
    }
}