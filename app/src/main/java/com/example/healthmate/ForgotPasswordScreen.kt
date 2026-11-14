package com.example.healthmate

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Allows a user to reset their password before signing in.
@Composable
fun ForgotPasswordScreen(
    error: String,
    onResetPassword: (email: String, newPassword: String) -> Boolean,
    onBackToLogin: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var validationError by rememberSaveable { mutableStateOf("") }
    var success by rememberSaveable { mutableStateOf(false) }

    AuthScaffold {
        BackText(onBackToLogin)
        Spacer(Modifier.height(22.dp))
        BrandMark(76.dp)
        Spacer(Modifier.height(12.dp))
        BrandTitle()
        Spacer(Modifier.height(28.dp))
        Heading(
            "Forgot Password?",
            "Enter your registered email address and choose a new password."
        )
        Spacer(Modifier.height(22.dp))

        AuthInput(
            email,
            { email = it; validationError = ""; success = false },
            "Email Address",
            androidx.compose.ui.text.input.KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))
        PasswordInput(
            newPassword,
            { newPassword = it; validationError = ""; success = false },
            "New Password"
        )
        Spacer(Modifier.height(12.dp))
        PasswordInput(
            confirmPassword,
            { confirmPassword = it; validationError = ""; success = false },
            "Confirm New Password"
        )
        Spacer(Modifier.height(12.dp))

        val message = validationError.ifBlank { error }
        if (message.isNotBlank()) ErrorText(message)
        if (success) {
            Text(
                "Password reset successfully. You can now log in with your new password.",
                color = HealthMateColors.Teal
            )
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton("Reset Password") {
            validationError = when {
                email.isBlank() -> "Please enter your email address."
                !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                    "Please enter a valid email address."
                newPassword.length < 8 ->
                    "New password must contain at least 8 characters."
                newPassword != confirmPassword ->
                    "New passwords do not match."
                !onResetPassword(email.trim(), newPassword) ->
                    "No account was found for this email address."
                else -> ""
            }

            success = validationError.isEmpty()
            if (success) {
                newPassword = ""
                confirmPassword = ""
            }
        }

        Spacer(Modifier.height(18.dp))
        FooterSwitch("Remembered your password?", "Back to Login", onBackToLogin)
    }
}
