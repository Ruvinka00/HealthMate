package com.example.healthmate

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext


// Different screens available in the application
private enum class AuthPage {
    Welcome,
    Login,
    Register,
    ForgotPassword,
    ChangePassword,
    Success,
    Dashboard
}

// Main Android activity for HealthMate application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(
                primary = HealthMateColors.Teal,
                onPrimary = Color.White,
                background = Color.White,
                surface = Color.White,
                onSurface = HealthMateColors.Dark
            )) {
                HealthMateApp { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
// Main function that controls the application screens
@Composable
private fun HealthMateApp(showMessage: (String) -> Unit) {
    val context = LocalContext.current
    val database = remember { HealthMateDatabase(context.applicationContext) }
    var page by rememberSaveable { mutableStateOf(AuthPage.Welcome) }
    var signedInEmail by rememberSaveable { mutableStateOf("") }
    var signedInUserId by rememberSaveable { mutableLongStateOf(0L) }
    val repository = remember(database) { HealthRepository(database) }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }

    when (page) {
        AuthPage.Welcome -> WelcomeScreen(
            onLogin = { error = ""; page = AuthPage.Login },
            onRegister = { error = ""; page = AuthPage.Register }
        )
        AuthPage.Login -> LoginScreen(
            email = email,
            onEmailChange = { email = it; error = "" },
            password = password,
            onPasswordChange = { password = it; error = "" },
            error = error,
            onLogin = {
                error = when {
                    email.isBlank() -> "Please enter your email address."
                    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Please enter a valid email address."
                    password.isBlank() -> "Please enter your password."
                    else -> ""
                }
                if (error.isEmpty()) {
                    val user = database.login(email.trim(), password)
                    if (user == null) error = "Login failed. Register this email first, or check your email and password."
                    else {
                        signedInEmail = user.username
                        signedInUserId = user.id
                        password = ""
                        error = ""
                        page = AuthPage.Dashboard
                    }
                }
            },
            onForgot = { error = ""; page = AuthPage.ForgotPassword },
            onRegister = { error = ""; page = AuthPage.Register }
        )
        AuthPage.Register -> RegisterScreen(
            name = fullName,
            onNameChange = { fullName = it; error = "" },
            email = email,
            onEmailChange = { email = it; error = "" },
            password = password,
            onPasswordChange = { password = it; error = "" },
            confirmPassword = confirmPassword,
            onConfirmChange = { confirmPassword = it; error = "" },
            error = error,
            onRegister = {
                error = when {
                    fullName.isBlank() -> "Please enter your full name."
                    email.isBlank() -> "Please enter your email address."
                    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Please enter a valid email address."
                    password.length < 8 -> "Use a password with at least 8 characters."
                    password != confirmPassword -> "Passwords do not match."
                    else -> ""
                }
                if (error.isEmpty()) {
                    // SQLite stores email in the existing username column.
                    val user = database.register(email.trim(), password)
                    if (user == null) error = "Registration failed. This email may already exist. Try logging in instead."
                    else {
    // Save the full name entered during registration.
    database.saveProfile(
        user.id,
        UserProfile(
            name = fullName.trim(),
            photoPath = "",
            stepGoal = 8000,
            waterGoal = 2000,
            calorieGoal = 500
        )
    )

    signedInEmail = user.username
    signedInUserId = user.id
    password = ""
    confirmPassword = ""
    error = ""
    page = AuthPage.Success
}
                }
            },
            onLogin = { error = ""; page = AuthPage.Login }
        )
        AuthPage.ForgotPassword -> ForgotPasswordScreen(
            error = error,
            onResetPassword = { resetEmail, newPassword ->
                val reset = repository.resetPassword(resetEmail, newPassword)
                if (reset) {
                    email = resetEmail
                    password = ""
                    error = ""
                }
                reset
            },
            onBackToLogin = {
                error = ""
                password = ""
                page = AuthPage.Login
            }
        )
        AuthPage.ChangePassword -> ResetPasswordScreen(
            error = error,
            onChangePassword = { current, new ->
                val changed = database.updatePassword(signedInUserId, current, new)
                error = if (changed) "" else ""
                changed
            },
            onBack = { error = ""; page = AuthPage.Dashboard }
        )
        AuthPage.Success -> SuccessScreen(onGetStarted = { error = ""; page = AuthPage.Login })
        AuthPage.Dashboard -> DashboardScreen(
            email = signedInEmail,
            userId = signedInUserId,
            repository = repository,
            onLogout = {
                email = ""; password = ""; confirmPassword = ""; signedInEmail = ""; signedInUserId = 0L; error = ""
                page = AuthPage.Welcome
            },
            onChangePassword = { error = ""; page = AuthPage.ChangePassword }
        )
    }
}
