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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthmate.HealthMateColors.Teal
import com.example.healthmate.HealthMateColors.DeepTeal

@Composable
fun SuccessScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Teal, DeepTeal)))
            .verticalScroll(rememberScrollState())
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BrandMark(92.dp)
        Spacer(Modifier.height(14.dp))
        BrandTitle()
        Spacer(Modifier.height(42.dp))
        Text("✓", fontSize = 64.sp, color = Color.White)
        Text("You're All Set!", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = Color.White)
        Spacer(Modifier.height(12.dp))
        Text(
            "Your account has been successfully created.\nStart your journey to a healthier you!",
            color = Color.White, textAlign = TextAlign.Center, fontSize = 15.sp,
            lineHeight = 23.sp
        )
        Spacer(Modifier.height(36.dp))
        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White, contentColor = DeepTeal
            ),
            shape = RoundedCornerShape(18.dp)
        ) { Text("Get Started   →", fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(44.dp))
        Text("🏃       🍎       ♥", fontSize = 28.sp)
        Text("Track Activities      Healthy Nutrition      Better Lifestyle",
            color = Color.White, textAlign = TextAlign.Center, fontSize = 12.sp)
    }
}

