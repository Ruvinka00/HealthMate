package com.example.healthmate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmate.HealthMateColors.Teal
import com.example.healthmate.HealthMateColors.Dark
import com.example.healthmate.HealthMateColors.Muted
import com.example.healthmate.HealthMateColors.Pale

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0FCF9), Color.White, Pale)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BrandMark(104.dp)
        Spacer(Modifier.height(12.dp))
        BrandTitle()
        Text("Better Health  •  Happier You", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.verticalGradient(listOf(Pale, Color.White))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🌿       🏃       🌿", fontSize = 38.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Build better habits.\nLive a healthier life.",
                    color = Dark, fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center, lineHeight = 29.sp
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Track your health and take control of your wellbeing.",
            color = Muted, textAlign = TextAlign.Center, fontSize = 15.sp)
        Spacer(Modifier.height(28.dp))
        PrimaryButton("Get Started", onRegister)
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) { Text("I already have an account") }
        Spacer(Modifier.height(20.dp))
        Text("Your health journey starts here.", color = Teal, fontSize = 13.sp)
    }
}

