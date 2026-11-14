package com.example.healthmate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.HealthMateColors.Dark

//Login Screen Layout
@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String, onLogin: () -> Unit,
    onForgot: () -> Unit,
    onRegister: () -> Unit
) {
    // Creates the main authentication screen
    AuthScaffold {

        // HealthMate logo
        BrandMark(68.dp,)
        Spacer(Modifier.height(10.dp))
        BrandTitle()
        Spacer(Modifier.height(24.dp))
        Heading("Welcome back!", "Login to continue your health journey.")
        Spacer(Modifier.height(26.dp))
        AuthInput(email, onEmailChange, "Email Address", KeyboardType.Email)
        Spacer(Modifier.height(14.dp))
        PasswordInput(password, onPasswordChange, "Password")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onForgot) { Text("Forgot Password?") }
        }
        // Displays login error message
        ErrorText(error)

        Spacer(Modifier.height(8.dp))

        // Displays the login button
        PrimaryButton("Log In", onLogin)
        Spacer(Modifier.height(18.dp))
        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.height(18.dp))

        // Displays a message and button to navigate to registration
        FooterSwitch("Don't have an account?", "Register", onRegister)
    }
}

