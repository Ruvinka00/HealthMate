package com.example.healthmate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration

private val activities = listOf("Walking", "Running", "Cycling", "Yoga", "Gym")
private val moods = listOf("Happy", "Good", "Neutral", "Stressed", "Tired")
private val qualities = listOf("Excellent", "Good", "Fair", "Poor")

@Composable
fun WellnessScreen(userId: Long, repository: HealthRepository) {
    var section by remember { mutableIntStateOf(0) }
    var revision by remember { mutableIntStateOf(0) }
    var editing by remember { mutableStateOf<WellnessLog?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<WellnessLog?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val table = listOf("sleep_logs", "exercise_logs", "mood_logs")[section]
    val logs = remember(userId, table, revision) { repository.wellness(table, userId) }
    val accent = listOf(Color(0xFF7C3AED), Color(0xFF0EA5E9), Color(0xFFF59E0B))[section]
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
        Text("My Wellness Journal ✨", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = accent)
        Text("Every little step counts. Capture your day!", color = Color(0xFF64748B))
        ScrollableTabRow(selectedTabIndex = section, edgePadding = 0.dp) {
            listOf("Sleep", "Exercise", "Mood").forEachIndexed { index, label ->
                Tab(selected = section == index, onClick = { section = index; editing = null }, text = { Text(label) })
            }
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = { editing = null; showForm = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = accent)) {
            Text("+ Add ${listOf("sleep", "exercise", "mood")[section]} entry")
        }
        if (logs.isEmpty()) Text("No entries yet. Add your first entry.", modifier = Modifier.padding(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(logs, key = { it.id }) { log ->
                val imageId = when (section) {
                    0 -> sleepImage(log.fields["quality"].orEmpty())
                    1 -> exerciseImage(log.fields["activity"].orEmpty())
                    else -> moodImage(log.fields["mood"].orEmpty())
                }
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = listOf(Color(0xFFF2EAFF), Color(0xFFE5F6FF), Color(0xFFFFF3DE))[section]),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Image(painter = painterResource(imageId), contentDescription = "Illustration for saved ${listOf("sleep quality", "exercise", "mood")[section]}", modifier = Modifier.size(88.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(log.date, fontWeight = FontWeight.Bold, color = accent)
                            Text(when (section) {
                                0 -> "${log.fields["quality"]} sleep • ${log.fields["minutes"]?.toIntOrNull()?.div(60) ?: 0}h ${log.fields["minutes"]?.toIntOrNull()?.rem(60) ?: 0}m"
                                1 -> "${log.fields["activity"]} • ${log.fields["duration"]} min"
                                else -> "Feeling ${log.fields["mood"]}"
                            }, fontWeight = FontWeight.SemiBold)
                            if (section == 0) Text("${log.fields["bedtime"]} → ${log.fields["wake_time"]}")
                            if (!log.fields["notes"].isNullOrBlank()) Text(log.fields["notes"].orEmpty(), style = MaterialTheme.typography.bodySmall)
                            Row {
                                TextButton(onClick = { editing = log; showForm = true }) { Text("Edit") }
                                TextButton(onClick = { deleting = log }) { Text("Delete", color = Color(0xFFDC2626)) }
                            }
                        }
                    }
                }
            }
        }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
    if (showForm) WellnessEditor(section, editing, onDismiss = { showForm = false }, onSave = { values ->
        val isEditing = editing != null
        val ok = repository.saveWellness(table, userId, editing?.id, values)
        if (ok) {
            revision++
            showForm = false
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    if (isEditing) "Wellness entry updated successfully!"
                    else "Wellness entry added successfully!"
                )
            }
        }
        ok
    })
    deleting?.let { log ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Delete entry?") }, text = { Text("This action cannot be undone.") }, confirmButton = {
            TextButton(onClick = {
                val deleted = repository.deleteWellness(table, userId, log.id)
                if (deleted) {
                    revision++
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Wellness entry deleted successfully!")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Could not delete entry. Please retry.")
                    }
                }
                deleting = null
            }) { Text("Delete") }
        }, dismissButton = { TextButton(onClick = { deleting = null }) { Text("Cancel") } })
    }
}

@Composable
private fun WellnessEditor(section: Int, record: WellnessLog?, onDismiss: () -> Unit, onSave: (Map<String, String>) -> Boolean) {
    var date by remember(section, record) { mutableStateOf(record?.date ?: LocalDate.now().toString()) }
    var bedtime by remember(section, record) { mutableStateOf(record?.fields?.get("bedtime") ?: "22:00") }
    var wake by remember(section, record) { mutableStateOf(record?.fields?.get("wake_time") ?: "06:00") }
    var quality by remember(section, record) { mutableStateOf(record?.fields?.get("quality") ?: "Good") }
    var activity by remember(section, record) { mutableStateOf(record?.fields?.get("activity") ?: "Walking") }
    var duration by remember(section, record) { mutableStateOf(record?.fields?.get("duration") ?: "30") }
    var mood by remember(section, record) { mutableStateOf(record?.fields?.get("mood") ?: "Good") }
    var notes by remember(section, record) { mutableStateOf(record?.fields?.get("notes") ?: "") }
    var error by remember(section, record) { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (record == null) "New wellness entry" else "Edit wellness entry") }, text = {
        Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(date, { date = it }, label = { Text("Date YYYY-MM-DD") }, singleLine = true)
            when (section) {
                0 -> {
                    OutlinedTextField(bedtime, { bedtime = it }, label = { Text("Bedtime HH:mm") }, singleLine = true)
                    OutlinedTextField(wake, { wake = it }, label = { Text("Wake time HH:mm") }, singleLine = true)
                    ChoiceField("Sleep quality", qualities, quality) { quality = it }
                }
                1 -> {
                    ChoiceField("Exercise", activities, activity) { activity = it }
                    OutlinedTextField(duration, { duration = it }, label = { Text("Duration (minutes)") }, singleLine = true)
                    OutlinedTextField(notes, { notes = it }, label = { Text("Notes") })
                }
                else -> {
                    ChoiceField("Mood", moods, mood) { mood = it }
                    OutlinedTextField(notes, { notes = it }, label = { Text("Notes") })
                }
            }
            val previewImage = when (section) {
                0 -> sleepImage(quality)
                1 -> exerciseImage(activity)
                else -> moodImage(mood)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Image(painter = painterResource(previewImage), contentDescription = "Selected entry illustration", modifier = Modifier.size(88.dp))
                Text("Your saved entry will show this illustration.", style = MaterialTheme.typography.bodySmall)
            }
            if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        }
    }, confirmButton = {
        TextButton(onClick = {
            if (runCatching { LocalDate.parse(date) }.isFailure) { error = "Enter a valid date."; return@TextButton }
            val fields = when (section) {
                0 -> {
                    val times = runCatching { LocalTime.parse(bedtime) to LocalTime.parse(wake) }.getOrNull()
                    if (times == null) { error = "Enter times in HH:mm format."; return@TextButton }
                    var minutes = Duration.between(times.first, times.second).toMinutes().toInt()
                    if (minutes <= 0) minutes += 1440
                    mapOf("date" to date, "bedtime" to bedtime, "wake_time" to wake, "minutes" to minutes.toString(), "quality" to quality)
                }
                1 -> {
                    val mins = duration.toIntOrNull()
                    if (mins == null || mins !in 1..1440) { error = "Duration must be 1–1440 minutes."; return@TextButton }
                    if (notes.length > 500) { error = "Notes must be at most 500 characters."; return@TextButton }
                    mapOf("date" to date, "activity" to activity, "duration" to duration, "notes" to notes)
                }
                else -> {
                    if (notes.length > 500) { error = "Notes must be at most 500 characters."; return@TextButton }
                    mapOf("date" to date, "mood" to mood, "notes" to notes)
                }
            }
            if (!onSave(fields)) error = "Could not save. Please retry."
        }) { Text("Save") }
    }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun ChoiceField(label: String, choices: List<String>, selected: String, onChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box {
            OutlinedButton(onClick = { expanded = true }) { Text("$selected ▾") }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                choices.forEach { choice ->
                    DropdownMenuItem(text = { Text(choice) }, onClick = { onChange(choice); expanded = false })
                }
            }
        }
    }
}

// The picture is derived from the saved entry, so it updates automatically after editing.
private fun sleepImage(value: String): Int = when (value) {
    "Excellent" -> R.drawable.sleep_excellent
    "Good" -> R.drawable.sleep_good
    "Fair" -> R.drawable.sleep_fair
    else -> R.drawable.sleep_poor
}
private fun exerciseImage(value: String): Int = when (value) {
    "Running" -> R.drawable.exercise_running
    "Cycling" -> R.drawable.exercise_cycling
    "Yoga" -> R.drawable.exercise_yoga
    "Gym" -> R.drawable.exercise_gym
    else -> R.drawable.exercise_walking
}
private fun moodImage(value: String): Int = when (value) {
    "Happy" -> R.drawable.mood_happy
    "Neutral" -> R.drawable.mood_neutral
    "Stressed" -> R.drawable.mood_stressed
    "Tired" -> R.drawable.mood_tired
    else -> R.drawable.mood_good
}
