package com.example.healthmate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Allows a signed-in user to change their password after verifying the current one.
@Composable
fun ResetPasswordScreen(
    error: String,
    onChangePassword: (currentPassword: String, newPassword: String) -> Boolean,
    onBack: () -> Unit
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var validationError by rememberSaveable { mutableStateOf("") }
    var success by rememberSaveable { mutableStateOf(false) }

    AuthScaffold {
        BackText(onBack)
        Spacer(Modifier.height(22.dp))
        BrandMark(76.dp)
        Spacer(Modifier.height(12.dp))
        BrandTitle()
        Spacer(Modifier.height(28.dp))
        Heading("Change Your Password", "Verify your current password and enter a new one.")
        Spacer(Modifier.height(22.dp))
        PasswordInput(currentPassword, { currentPassword = it; validationError = ""; success = false }, "Current Password")
        Spacer(Modifier.height(12.dp))
        PasswordInput(newPassword, { newPassword = it; validationError = ""; success = false }, "New Password")
        Spacer(Modifier.height(12.dp))
        PasswordInput(confirmPassword, { confirmPassword = it; validationError = ""; success = false }, "Confirm New Password")
        Spacer(Modifier.height(12.dp))
        val message = validationError.ifBlank { error }
        if (message.isNotBlank()) ErrorText(message)
        if (success) Text("Password changed successfully.", color = HealthMateColors.Teal)
        Spacer(Modifier.height(20.dp))
        PrimaryButton("Change Password") {
            validationError = when {
                currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> "Please complete all password fields."
                newPassword.length < 8 -> "New password must contain at least 8 characters."
                newPassword == currentPassword -> "Choose a password different from your current password."
                newPassword != confirmPassword -> "New passwords do not match."
                !onChangePassword(currentPassword, newPassword) -> "Current password is incorrect. Please try again."
                else -> ""
            }
            success = validationError.isEmpty()
            if (success) {
                currentPassword = ""; newPassword = ""; confirmPassword = ""
            }
        }
        Spacer(Modifier.height(18.dp))
        FooterSwitch("Finished?", "Back to Profile", onBack)
    }
}
