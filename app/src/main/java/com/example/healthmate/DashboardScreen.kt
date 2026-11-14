
package com.example.healthmate

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.util.UUID
import java.time.LocalDate

// Main dashboard screen of the HealthMate application.

private val Aqua = Color(0xFF008C92)
private val Background = Color(0xFFF0F9FA)

@Composable
fun DashboardScreen(
    email: String,
    userId: Long,
    repository: HealthRepository,
    onLogout: () -> Unit,
    onChangePassword: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    var revision by remember { mutableIntStateOf(0) }
    var filter by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf<HealthRecord?>(null) }
    var adding by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<HealthRecord?>(null) }

    // Stores success and error messages for health record operations.
    var recordMessage by remember { mutableStateOf("") }

    var profile by remember(userId) {
        mutableStateOf(repository.profile(userId))
    }

    val records = remember(userId, revision) {
        repository.records(userId)
    }

    val today = LocalDate.now().toString()
    val todays = records.filter { it.date == today }
    val steps = todays.sumOf { it.steps }
    val calories = todays.sumOf { it.calories }
    val water = todays.sumOf { it.water }

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(
            "HealthMate",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Aqua
        )

        Text(
            profile.name.ifBlank { email },
            fontSize = 13.sp,
            color = Color.DarkGray
        )

        Spacer(Modifier.height(12.dp))

        ScrollableTabRow(
            selectedTabIndex = tab,
            edgePadding = 0.dp
        ) {
            listOf(
                "Dashboard",
                "Records",
                "Wellness",
                "Monthly Report",
                "Profile"
            ).forEachIndexed { index, title ->
                Tab(
                    selected = tab == index,
                    onClick = { tab = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        if (tab == 0) {

            val scroll = rememberScrollState()

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(scroll)
            ) {
                Text(
                    "Today • $today",
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(12.dp))

                Metric(
                    "👣 Steps walked",
                    "$steps",
                    Color(0xFFE2F4EA)
                )

                Metric(
                    "🔥 Calories burned",
                    "$calories kcal",
                    Color(0xFFFFE8E5)
                )

                Metric(
                    "💧 Water intake",
                    "$water ml",
                    Color(0xFFDCEFFF)
                )

                Text(
                    "Today's totals are calculated from your saved records.",
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        recordMessage = ""
                        editing = null
                        adding = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Add health record")
                }

                OutlinedButton(
                    onClick = { tab = 1 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View past records")
                }

                Spacer(Modifier.height(8.dp))

                ActivityTrend(records, profile)

                OutlinedButton(
                    onClick = { tab = 4 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit profile & photo")
                }
            }

        } else if (tab == 1) {

            OutlinedTextField(
                value = filter,
                onValueChange = { filter = it },
                label = { Text("Filter date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    recordMessage = ""
                    editing = null
                    adding = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add record")
            }

            val shown = records.filter {
                filter.isBlank() ||
                    it.date.contains(filter.trim(), ignoreCase = true)
            }

            if (shown.isEmpty()) {
                Text(
                    "No matching records found.",
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(shown, key = { it.id }) { record ->

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                record.date,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "👣 ${record.steps} steps   🔥 ${record.calories} kcal"
                            )

                            Text("💧 ${record.water} ml")

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        recordMessage = ""
                                        editing = record
                                        adding = true
                                    }
                                ) {
                                    Text("Edit")
                                }

                                TextButton(
                                    onClick = {
                                        recordMessage = ""
                                        deleting = record
                                    }
                                ) {
                                    Text(
                                        "Delete",
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }

        } else if (tab == 2) {

            WellnessScreen(userId, repository)

        } else if (tab == 3) {

            MonthlyReportScreen(userId, repository, profile)

        } else {

            // Keep the complete profile form scrollable on smaller screens,
            // so Change Password and Log out remain reachable.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileEditor(
                    profile,
                    email,
                    onSave = { updated ->
                        if (repository.saveProfile(userId, updated)) {
                            profile = updated
                            true
                        } else {
                            false
                        }
                    },
                    onLogout = onLogout,
                    onChangePassword = onChangePassword
                )
            }
        }
    }

    // Add or update health record.
    if (adding) {
        RecordEditor(
            record = editing,
            onDismiss = {
                adding = false
            },
            onSave = { date, s, c, w ->

                val isEditing = editing != null

                val ok = repository.save(
                    userId,
                    editing?.id,
                    date,
                    s,
                    c,
                    w
                )

                if (ok) {
                    revision++

                    recordMessage = if (isEditing) {
                        "Health record updated successfully!"
                    } else {
                        "Health record added successfully!"
                    }

                    adding = false
                }

                ok
            }
        )
    }

    // Delete health record confirmation.
    deleting?.let { record ->

        AlertDialog(
            onDismissRequest = {
                deleting = null
            },

            title = {
                Text("Delete record?")
            },

            text = {
                Text(
                    "Delete the record dated ${record.date}? This cannot be undone."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {

                        if (repository.delete(userId, record.id)) {
                            revision++
                            recordMessage =
                                "Health record deleted successfully!"
                        } else {
                            recordMessage =
                                "Could not delete the health record."
                        }

                        deleting = null
                    }
                ) {
                    Text("Delete")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        deleting = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Display the success or failure pop-up.
    if (recordMessage.isNotBlank()) {

        AlertDialog(
            onDismissRequest = {
                recordMessage = ""
            },

            icon = {
                Text(
                    if (recordMessage.startsWith("Could not")) {
                        "⚠️"
                    } else {
                        "✅"
                    },
                    fontSize = 30.sp
                )
            },

            title = {
                Text(
                    if (recordMessage.startsWith("Could not")) {
                        "Operation Failed"
                    } else {
                        "Success!"
                    }
                )
            },

            text = {
                Text(recordMessage)
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        recordMessage = ""
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

// Displays dashboard health metrics.
@Composable
private fun Metric(
    title: String,
    value: String,
    tint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = tint
        )
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(title)

            Text(
                value,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Add and edit health record form.
@Composable
private fun RecordEditor(
    record: HealthRecord?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int) -> Boolean
) {
    var date by remember(record) {
        mutableStateOf(record?.date ?: LocalDate.now().toString())
    }

    var steps by remember(record) {
        mutableStateOf(record?.steps?.toString() ?: "")
    }

    var calories by remember(record) {
        mutableStateOf(record?.calories?.toString() ?: "")
    }

    var water by remember(record) {
        mutableStateOf(record?.water?.toString() ?: "")
    }

    var error by remember(record) {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                if (record == null) {
                    "Add health record"
                } else {
                    "Edit health record"
                }
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                OutlinedTextField(
                    date,
                    { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true
                )

                OutlinedTextField(
                    steps,
                    { steps = it },
                    label = { Text("Steps walked") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    calories,
                    { calories = it },
                    label = { Text("Calories burned (kcal)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    water,
                    { water = it },
                    label = { Text("Water intake (ml)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                if (error.isNotBlank()) {
                    Text(
                        error,
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
            }
        },

        confirmButton = {
            TextButton(
                onClick = {

                    val s = steps.toIntOrNull()
                    val c = calories.toIntOrNull()
                    val w = water.toIntOrNull()

                    error = when {

                        runCatching {
                            LocalDate.parse(date)
                        }.isFailure -> {
                            "Enter a valid date as YYYY-MM-DD."
                        }

                        s == null || c == null || w == null -> {
                            "Enter whole numbers for all three activities."
                        }

                        s < 0 || c < 0 || w < 0 -> {
                            "Values cannot be negative."
                        }

                        !onSave(date, s, c, w) -> {
                            "Could not save the record. Please retry."
                        }

                        else -> ""
                    }
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// User profile editor.
@Composable
private fun ProfileEditor(
    profile: UserProfile,
    email: String,
    onSave: (UserProfile) -> Boolean,
    onLogout: () -> Unit,
    onChangePassword: () -> Unit
) {
    val context = LocalContext.current

    var name by remember(profile) {
        mutableStateOf(profile.name)
    }

    var stepGoal by remember(profile) {
        mutableStateOf(profile.stepGoal.toString())
    }

    var waterGoal by remember(profile) {
        mutableStateOf(profile.waterGoal.toString())
    }

    var calorieGoal by remember(profile) {
        mutableStateOf(profile.calorieGoal.toString())
    }

    var photoPath by remember(profile) {
        mutableStateOf(profile.photoPath)
    }

    var message by remember {
        mutableStateOf("")
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            try {
                val file = File(
                    context.filesDir,
                    "healthmate_profile_${UUID.randomUUID()}.jpg"
                )

                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                if (file.exists() && file.length() > 0) {
                    photoPath = file.absolutePath
                } else {
                    message = "Unable to read photo."
                }

            } catch (_: Exception) {
                message = "Unable to save photo."
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "My profile",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )

        ProfilePhoto(photoPath)

        OutlinedButton(
            onClick = {
                picker.launch("image/*")
            }
        ) {
            Text("Choose profile picture")
        }

        Text(
            "Account: $email",
            fontSize = 13.sp
        )

        OutlinedTextField(
            name,
            { name = it },
            label = { Text("Display name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            stepGoal,
            { stepGoal = it },
            label = { Text("Daily step goal") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            waterGoal,
            { waterGoal = it },
            label = { Text("Daily water goal (ml)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            calorieGoal,
            { calorieGoal = it },
            label = { Text("Daily calorie goal (kcal)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {

                val s = stepGoal.toIntOrNull()
                val w = waterGoal.toIntOrNull()
                val c = calorieGoal.toIntOrNull()

                message = when {

                    name.isBlank() || name.length > 80 -> {
                        "Enter a display name (up to 80 characters)."
                    }

                    s == null || s !in 1..100000 -> {
                        "Enter a step goal from 1 to 100,000."
                    }

                    w == null || w !in 1..10000 -> {
                        "Enter a water goal from 1 to 10,000 ml."
                    }

                    c == null || c !in 1..10000 -> {
                        "Enter a calorie goal from 1 to 10,000."
                    }

                    onSave(
                        UserProfile(
                            name.trim(),
                            photoPath,
                            s,
                            w,
                            c
                        )
                    ) -> {
                        "Profile saved."
                    }

                    else -> {
                        "Could not save profile."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save profile")
        }

        if (message.isNotBlank()) {
            Text(
                message,
                color = Aqua
            )
        }

        OutlinedButton(
            onClick = onChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Change password")
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log out")
        }
    }
}

// Displays the user's profile picture.
@Composable
private fun ProfilePhoto(path: String) {
    val bitmap = remember(path) {
        if (path.isBlank()) {
            null
        } else {
            runCatching {
                BitmapFactory.decodeFile(path)?.asImageBitmap()
            }.getOrNull()
        }
    }

    if (bitmap != null) {
        Image(
            bitmap,
            contentDescription = "Profile photo",
            modifier = Modifier.size(88.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Text(
            "👤",
            fontSize = 55.sp
        )
    }
}

// Displays the seven-day activity chart and goal status.
@Composable
private fun ActivityTrend(
    records: List<HealthRecord>,
    profile: UserProfile
) {
    val today = LocalDate.now()

    val days = (6 downTo 0).map {
        today.minusDays(it.toLong())
    }

    val stepValues = days.map { day ->
        records.filter {
            it.date == day.toString()
        }.sumOf { it.steps }
    }

    val waterValues = days.map { day ->
        records.filter {
            it.date == day.toString()
        }.sumOf { it.water }
    }

    val todaySteps = stepValues.last()
    val todayWater = waterValues.last()

    val hasToday = records.any {
        it.date == today.toString()
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "7-day activity chart",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                "👣 Steps • goal ${profile.stepGoal}",
                color = Aqua
            )

            TrendLine(
                stepValues,
                profile.stepGoal,
                Aqua
            )

            Text(
                "💧 Water (ml) • goal ${profile.waterGoal}",
                color = Color(0xFF1769AA)
            )

            TrendLine(
                waterValues,
                profile.waterGoal,
                Color(0xFF1769AA)
            )

            Text(
                days.joinToString("     ") {
                    it.dayOfMonth.toString()
                },
                fontSize = 11.sp
            )

            val label = when {

                !hasToday ->
                    "⚪ No activity logged today"

                todaySteps >= profile.stepGoal &&
                    todayWater >= profile.waterGoal ->
                    "🟢 Daily activity goals reached"

                todaySteps >= profile.stepGoal ||
                    todayWater >= profile.waterGoal ->
                    "🟠 One daily goal still pending"

                else ->
                    "🔴 Below both daily activity goals"
            }

            Text(
                label,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "Red means below your chosen goals, not a medical danger zone. High activity alone does not establish a health risk.",
                fontSize = 11.sp
            )
        }
    }
}

// Draws the activity trend line.
@Composable
private fun TrendLine(
    values: List<Int>,
    goal: Int,
    color: Color
) {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        val maxVal = maxOf(
            goal,
            values.maxOrNull() ?: 0,
            1
        ).toFloat() * 1.15f

        val y = { v: Int ->
            size.height * (1f - v / maxVal)
        }

        val goalY = y(goal)

        drawLine(
            Color.LightGray,
            Offset(0f, goalY),
            Offset(size.width, goalY),
            strokeWidth = 2.dp.toPx()
        )

        val path = Path()

        values.forEachIndexed { i, value ->
            val x = size.width * i / 6f

            if (i == 0) {
                path.moveTo(x, y(value))
            } else {
                path.lineTo(x, y(value))
            }
        }

        drawPath(
            path,
            color,
            style = Stroke(width = 3.dp.toPx())
        )

        values.forEachIndexed { i, value ->
            drawCircle(
                color,
                radius = 3.dp.toPx(),
                center = Offset(
                    size.width * i / 6f,
                    y(value)
                )
            )
        }
    }
}

// Step goal circular indicator.
@Composable
private fun GoalCircle(
    steps: Int,
    goal: Int
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = {
                (steps.toFloat() / goal).coerceIn(0f, 1f)
            },
            modifier = Modifier.size(110.dp),
            strokeWidth = 10.dp,
            color = Aqua
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "${(steps.toFloat() * 100 / goal).toInt()}%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Step goal",
                fontSize = 12.sp
            )
        }
    }
}

