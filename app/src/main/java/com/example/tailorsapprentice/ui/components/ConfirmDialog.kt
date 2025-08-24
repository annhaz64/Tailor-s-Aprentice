package com.example.tailorsapprentice.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

/**
 * Small reusable confirmation dialog.
 *
 * title: dialog title
 * message: dialog message/body
 * confirmText: text for confirm button (e.g., "Delete")
 * dismissText: text for cancel/dismiss button (e.g., "Cancel")
 * onConfirm: invoked when user confirms
 * onDismiss: invoked when user cancels/dismisses
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(confirmText) }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(dismissText) }
        }
    )
}