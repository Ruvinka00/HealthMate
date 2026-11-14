package com.example.healthmate

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Import commonly used HealthMate theme colors
import com.example.healthmate.HealthMateColors.Teal
import com.example.healthmate.HealthMateColors.Dark
import com.example.healthmate.HealthMateColors.Muted


// Creates the common layout for authentication screens.
// Provides a gradient background, scrolling and centered content.
@Composable
fun AuthScaffold(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF5FCFA), Color.White)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content
    )
}

// Displays a heading with a title and subtitle.
@Composable
fun Heading(title: String, subtitle: String) {
    Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold,
        color = Dark, textAlign = TextAlign.Center)
    Spacer(Modifier.height(8.dp))
    Text(subtitle, fontSize = 14.sp, color = Muted,
        textAlign = TextAlign.Center, lineHeight = 21.sp)
}

// Creates a reusable text input field.
@Composable
fun AuthInput(
    value: String, onValueChange: (String) -> Unit, label: String,
    keyboardType: KeyboardType, enabled: Boolean = true
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        enabled = enabled, label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = Color(0xFFE0E8EA),
            focusedLabelColor = Teal
        )
    )
}

// Creates a password input field. It also allows the User to show or hide password
@Composable
fun PasswordInput(
    value: String, onValueChange: (String) -> Unit, label: String
) {
    var visible by rememberSaveable(label) { mutableStateOf(false) }
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(15.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (visible) VisualTransformation.None
            else PasswordVisualTransformation(),
        trailingIcon = {
            TextButton(onClick = { visible = !visible }) {
                Text(if (visible) "Hide" else "Show")
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = Color(0xFFE0E8EA),
            focusedLabelColor = Teal
        )
    )
}

// Creates the main action button (Login/Register/Continue).
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Teal)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Displays a message with a clickable action.
@Composable
fun FooterSwitch(prefix: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(prefix, color = Muted, fontSize = 13.sp)
        TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 6.dp)) {
            Text(action, color = Teal, fontWeight = FontWeight.Bold)
        }
    }
}

// Useful when separating different login methods.
@Composable
fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE3E9EA))
        Text("   or   ", color = Muted, fontSize = 13.sp)
        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE3E9EA))
    }
}

// Displays an error message only when the error is not empty.
@Composable
fun ErrorText(error: String) {
    if (error.isNotBlank()) {
        Spacer(Modifier.height(6.dp))
        Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
    }
}

// Creates a clickable Back text.
@Composable
fun BackText(onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Text(
            "← Back",
            modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
            color = Teal, fontWeight = FontWeight.SemiBold
        )
    }
}

// Creates the HealthMate logo using the letters HM.
@Composable
fun BrandMark(size: Dp = 104.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(28.dp))
            .background(HealthMateColors.Teal),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "HM",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Displays the HealthMate application name.
@Composable
fun BrandTitle() {
    Text(
        text = "HealthMate",
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = HealthMateColors.DeepTeal
    )
}


