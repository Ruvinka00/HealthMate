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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ReportTeal = Color(0xFF008C92)
private val ReportBackground = Color(0xFFF0F9FA)

@Composable
fun MonthlyReportScreen(userId: Long, repository: HealthRepository, profile: UserProfile) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val monthKey = selectedMonth.toString()
    val records = repository.records(userId).filter { it.date.startsWith(monthKey) }
    val sleepLogs = repository.wellness("sleep_logs", userId).filter { it.date.startsWith(monthKey) }
    val exerciseLogs = repository.wellness("exercise_logs", userId).filter { it.date.startsWith(monthKey) }
    val moodLogs = repository.wellness("mood_logs", userId).filter { it.date.startsWith(monthKey) }

    val totalSteps = records.sumOf { it.steps }
    val totalCalories = records.sumOf { it.calories }
    val totalWater = records.sumOf { it.water }
    val loggedDays = records.map { it.date }.distinct().size
    val averageSteps = if (loggedDays > 0) totalSteps / loggedDays else 0
    val averageCalories = if (loggedDays > 0) totalCalories / loggedDays else 0
    val averageWater = if (loggedDays > 0) totalWater / loggedDays else 0
    val stepGoalDays = records.groupBy { it.date }.count { (_, dayRecords) -> dayRecords.sumOf { it.steps } >= profile.stepGoal }
    val waterGoalDays = records.groupBy { it.date }.count { (_, dayRecords) -> dayRecords.sumOf { it.water } >= profile.waterGoal }
    val totalSleepMinutes = sleepLogs.sumOf { it.fields["minutes"]?.toIntOrNull() ?: 0 }
    val averageSleepMinutes = if (sleepLogs.isNotEmpty()) totalSleepMinutes / sleepLogs.size else 0
    val totalExerciseMinutes = exerciseLogs.sumOf { it.fields["duration"]?.toIntOrNull() ?: 0 }
    val moodCounts = moodLogs.groupingBy { it.fields["mood"].orEmpty() }.eachCount()
    val mostCommonMood = moodCounts.maxByOrNull { it.value }?.key

    Column(
        Modifier.fillMaxSize().background(ReportBackground).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Monthly Health Report", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = ReportTeal)
        Text("A summary of your saved health and wellness activity.", fontSize = 13.sp, color = Color.DarkGray)

        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) { Text("‹ Previous") }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("${records.size} health record(s)", fontSize = 11.sp, color = Color.Gray)
                }
                TextButton(onClick = { if (selectedMonth.isBefore(YearMonth.now())) selectedMonth = selectedMonth.plusMonths(1) }, enabled = selectedMonth.isBefore(YearMonth.now())) { Text("Next ›") }
            }
        }

        if (records.isEmpty() && sleepLogs.isEmpty() && exerciseLogs.isEmpty() && moodLogs.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📊", fontSize = 38.sp)
                    Text("No report data for this month", fontWeight = FontWeight.Bold)
                    Text("Add health records or wellness entries to build your monthly report.", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            Text("Activity overview", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportMetric("👣 Steps", totalSteps.toString(), "Total", Color(0xFFE2F4EA), Modifier.weight(1f))
                ReportMetric("🔥 Calories", "$totalCalories", "kcal total", Color(0xFFFFE8E5), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportMetric("💧 Water", "$totalWater", "ml total", Color(0xFFDCEFFF), Modifier.weight(1f))
                ReportMetric("📅 Days", loggedDays.toString(), "Days logged", Color(0xFFF1E9FF), Modifier.weight(1f))
            }

            ReportSection("Daily averages") {
                ReportProgress("Average steps", "$averageSteps / day", averageSteps, profile.stepGoal, ReportTeal)
                ReportProgress("Average calories", "$averageCalories kcal / day", averageCalories, profile.calorieGoal, Color(0xFFE58A48))
                ReportProgress("Average water", "$averageWater ml / day", averageWater, profile.waterGoal, Color(0xFF3989C9))
                Text("Averages are calculated using ${if (loggedDays == 1) "the one day" else "$loggedDays days"} with saved health records, not every calendar day.", fontSize = 11.sp, color = Color.Gray)
            }

            ReportSection("Goal consistency") {
                ReportProgress("Step goal reached", "$stepGoalDays of $loggedDays logged days", stepGoalDays, loggedDays, ReportTeal)
                ReportProgress("Water goal reached", "$waterGoalDays of $loggedDays logged days", waterGoalDays, loggedDays, Color(0xFF3989C9))
            }

            ReportSection("Daily steps this month") {
                val daily = (1..selectedMonth.lengthOfMonth()).map { day ->
                    val date = selectedMonth.atDay(day).toString()
                    day to records.filter { it.date == date }.sumOf { it.steps }
                }
                daily.filter { it.second > 0 }.forEach { (day, value) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(day.toString().padStart(2, '0'), modifier = Modifier.width(28.dp), fontSize = 12.sp)
                        Box(Modifier.weight(1f).height(12.dp).background(Color(0xFFE7EEEE), RoundedCornerShape(20.dp))) {
                            Box(Modifier.fillMaxWidth((value.toFloat() / maxOf(profile.stepGoal, daily.maxOfOrNull { it.second } ?: 1, 1)).coerceIn(0f, 1f)).fillMaxHeight().background(ReportTeal, RoundedCornerShape(20.dp)))
                        }
                        Text(value.toString(), modifier = Modifier.width(62.dp), fontSize = 11.sp)
                    }
                }
                Text("Only dates with saved step records are displayed.", fontSize = 11.sp, color = Color.Gray)
            }

            ReportSection("Wellness summary") {
                ReportInfo("😴 Sleep", if (sleepLogs.isEmpty()) "No sleep entries" else "${sleepLogs.size} entries • average ${averageSleepMinutes / 60}h ${averageSleepMinutes % 60}m")
                ReportInfo("🏃 Exercise", if (exerciseLogs.isEmpty()) "No exercise entries" else "${exerciseLogs.size} sessions • $totalExerciseMinutes minutes total")
                ReportInfo("😊 Mood", if (mostCommonMood == null) "No mood entries" else "Most recorded mood: $mostCommonMood (${moodCounts[mostCommonMood]})")
            }
        }
    }
}

@Composable
private fun ReportMetric(title: String, value: String, subtitle: String, tint: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = tint), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontSize = 12.sp, color = Color.DarkGray)
            Text(value, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color(0xFF243638))
            Text(subtitle, fontSize = 10.sp, color = Color.DarkGray)
        }
    }
}

@Composable
private fun ReportSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            content()
        })
    }
}

@Composable
private fun ReportProgress(title: String, valueText: String, value: Int, goal: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 13.sp)
            Text(valueText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        LinearProgressIndicator(
            progress = { if (goal > 0) (value.toFloat() / goal).coerceIn(0f, 1f) else 0f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = Color(0xFFE8EEEE)
        )
    }
}

@Composable
private fun ReportInfo(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(value, fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.widthIn(max = 205.dp))
    }
}
