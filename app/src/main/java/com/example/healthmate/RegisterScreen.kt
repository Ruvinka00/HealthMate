package com.example.healthmate

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    confirmPassword: String, onConfirmChange: (String) -> Unit,
    error: String, onRegister: () -> Unit, onLogin: () -> Unit
) {
    AuthScaffold {
        BrandMark(64.dp,)
        Spacer(Modifier.height(8.dp))
        BrandTitle()
        Spacer(Modifier.height(18.dp))
        Heading("Create Your Account", "Join HealthMate and take control of your health.")
        Spacer(Modifier.height(22.dp))
        AuthInput(name, onNameChange, "Full Name", KeyboardType.Text)
        Spacer(Modifier.height(12.dp))
        AuthInput(email, onEmailChange, "Email Address", KeyboardType.Email)
        Spacer(Modifier.height(12.dp))
        PasswordInput(password, onPasswordChange, "Password")
        Spacer(Modifier.height(12.dp))
        PasswordInput(confirmPassword, onConfirmChange, "Confirm Password")
        Spacer(Modifier.height(12.dp))
        ErrorText(error)
        Spacer(Modifier.height(14.dp))
        PrimaryButton("Register", onRegister)
        Spacer(Modifier.height(14.dp))
        FooterSwitch("Already have an account?", "Log In", onLogin)
    }
}

