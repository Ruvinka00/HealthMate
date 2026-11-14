package com.example.healthmate

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDate

// Stores the user's profile information and daily activity goals.
data class UserProfile(
    val name: String,
    val photoPath: String,
    val stepGoal: Int,
    val waterGoal: Int,
    val calorieGoal: Int = 500
)

// Stores wellness activity records.
data class WellnessLog(
    val id: Long,
    val date: String,
    val fields: Map<String, String>
)

// Stores the logged-in user's account information
data class HealthUser(
    val id: Long,
    val username: String
)

// Stores the user's daily health activity information.
data class HealthRecord(
    val id: Long,
    val userId: Long,
    val date: String,
    val steps: Int,
    val calories: Int,
    val water: Int
)

// Manages the HealthMate SQLite database operations
class HealthMateDatabase(context: Context) : SQLiteOpenHelper(
    context,
    "healthmate.db",
    null,
    4
)
{
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true)
    }

    // Creates the database tables.
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username " +
                "TEXT NOT NULL UNIQUE COLLATE NOCASE, password_hash TEXT NOT NULL)")
        createRecords(db)
        createProfiles(db)
        createWellnessTables(db)
    }

    // Creates the health records table.
    private fun createRecords(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE health_records (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, date TEXT NOT NULL, steps INTEGER NOT NULL CHECK(steps >= 0), calories " +
                "INTEGER NOT NULL CHECK(calories >= 0), water INTEGER NOT NULL CHECK(water >= 0), FOREIGN KEY(user_id) " +
                "REFERENCES users(id) ON DELETE CASCADE)")
        db.execSQL("CREATE INDEX idx_health_user_date ON health_records(user_id, date)")
    }

    // Creates the user profiles table.
    private fun createProfiles(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user_profiles (user_id INTEGER PRIMARY KEY, " +
                "display_name TEXT NOT NULL DEFAULT '', photo_path TEXT NOT NULL DEFAULT '', " +
                "step_goal INTEGER NOT NULL DEFAULT 8000, water_goal INTEGER NOT NULL DEFAULT 2000, " +
                "calorie_goal INTEGER NOT NULL DEFAULT 500, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)")
    }


    fun getProfile(userId: Long): UserProfile {
        readableDatabase.query("user_profiles", arrayOf("display_name", "photo_path", "step_goal", "water_goal", "calorie_goal"), "user_id = ?", arrayOf(userId.toString()), null, null, null).use { c ->
            if (c.moveToFirst()) return UserProfile(c.getString(0), c.getString(1), c.getInt(2), c.getInt(3), c.getInt(4))
        }
        return UserProfile("", "", 8000, 2000)
    }
    fun saveProfile(userId: Long, profile: UserProfile): Boolean {
        if (userId <= 0 || profile.name.length > 80 || profile.stepGoal !in 1..100000 || profile.waterGoal !in 1..10000 || profile.calorieGoal !in 1..10000) return false
        return writableDatabase.insertWithOnConflict("user_profiles", null, ContentValues().apply {
            put("user_id", userId);
            put("display_name", profile.name.trim());
            put("photo_path", profile.photoPath)
            put("step_goal", profile.stepGoal);
            put("water_goal", profile.waterGoal);
            put("calorie_goal", profile.calorieGoal)
        },
            SQLiteDatabase.CONFLICT_REPLACE) != -1L
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) createProfiles(db)
        if (oldVersion < 4) {
            if (oldVersion >= 3) db.execSQL("ALTER TABLE user_profiles ADD COLUMN calorie_goal INTEGER NOT NULL DEFAULT 500")
            createWellnessTables(db)
        }
        if (oldVersion < 2) {
            // Old records used a different measurement schema; keep user accounts.
            db.execSQL("DROP TABLE IF EXISTS health_records")
            createRecords(db)
        }
    }

    // Creates the wellness tables.
    private fun createWellnessTables(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS sleep_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, date TEXT NOT NULL, bedtime TEXT NOT NULL, wake_time TEXT NOT NULL, minutes INTEGER NOT NULL, quality TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS exercise_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, date TEXT NOT NULL, activity TEXT NOT NULL, duration INTEGER NOT NULL, notes TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)")
        db.execSQL("CREATE TABLE IF NOT EXISTS mood_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, date TEXT NOT NULL, mood TEXT NOT NULL, notes TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sleep_user ON sleep_logs(user_id, date)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_exercise_user ON exercise_logs(user_id, date)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mood_user ON mood_logs(user_id, date)")
    }
    fun wellnessLogs(table: String, userId: Long): List<WellnessLog> {
        require(table in setOf("sleep_logs", "exercise_logs", "mood_logs"))
        val result = mutableListOf<WellnessLog>()
        readableDatabase.query(table, null, "user_id=?", arrayOf(userId.toString()), null, null, "date DESC, id DESC").use { c ->
            while (c.moveToNext()) {
                val fields = (0 until c.columnCount).associate { c.getColumnName(it) to c.getString(it) }
                result.add(WellnessLog(c.getLong(c.getColumnIndexOrThrow("id")), fields["date"].orEmpty(), fields))
            }
        }
        return result
    }
    fun saveWellness(table: String, userId: Long, id: Long?, fields: Map<String, String>): Boolean {
        val allowed = when(table) {
            "sleep_logs" -> setOf("date", "bedtime", "wake_time", "minutes", "quality")
            "exercise_logs" -> setOf("date", "activity", "duration", "notes")
            "mood_logs" -> setOf("date", "mood", "notes")
            else -> return false
        }
        if (userId <= 0 || fields.keys != allowed || runCatching { LocalDate.parse(fields["date"]) }.isFailure) return false
        val values = ContentValues().apply { put("user_id", userId) }
        fields.forEach { (key, value) ->
            if (key in setOf("minutes", "duration")) {
                val number = value.toIntOrNull() ?: return false
                if (number <= 0) return false
                values.put(key, number)
            } else values.put(key, value)
        }
        return if (id == null) writableDatabase.insert(table, null, values) != -1L
        else writableDatabase.update(table, values, "id=? AND user_id=?", arrayOf(id.toString(), userId.toString())) > 0
    }
    fun deleteWellness(table: String, userId: Long, id: Long): Boolean {
        if (table !in setOf("sleep_logs", "exercise_logs", "mood_logs")) return false
        return writableDatabase.delete(table, "id=? AND user_id=?", arrayOf(id.toString(), userId.toString())) > 0
    }

    fun register(usernameInput: String, password: String): HealthUser? {
        val username = usernameInput.trim()
        if (username.length < 3 || password.length < 8) return null
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
        return try {
            val id = writableDatabase.insertOrThrow("users", null, ContentValues().apply {
                put("username", username); put("password_hash", "$salt:${digest(password, salt)}")
            })
            HealthUser(id, username)
        } catch (_: Exception) { null }
    }
    fun login(usernameInput: String, password: String): HealthUser? {
        readableDatabase.query("users", arrayOf("id", "username", "password_hash"), "username = ?", arrayOf(usernameInput.trim()), null, null, null).use { c ->
            if (!c.moveToFirst()) return null
            val parts = c.getString(c.getColumnIndexOrThrow("password_hash")).split(":", limit = 2)
            if (parts.size != 2 || digest(password, parts[0]) != parts[1]) return null
            return HealthUser(c.getLong(c.getColumnIndexOrThrow("id")), c.getString(c.getColumnIndexOrThrow("username")))
        }
    }
    private fun digest(password: String, salt: String): String = MessageDigest.getInstance("SHA-256").digest("$salt:$password".toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

    // Resets a password using the registered email address.
    // This local-only flow does not verify email ownership; see the report note.
    fun resetPassword(usernameInput: String, newPassword: String): Boolean {
        val username = usernameInput.trim()
        if (username.isBlank() || newPassword.length < 8) return false

        val userId = readableDatabase.query(
            "users", arrayOf("id"), "username = ?",
            arrayOf(username), null, null, null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return false
            cursor.getLong(cursor.getColumnIndexOrThrow("id"))
        }

        val newSalt = ByteArray(16).also { SecureRandom().nextBytes(it) }
            .joinToString("") { "%02x".format(it) }
        val newHash = "$newSalt:${digest(newPassword, newSalt)}"

        return writableDatabase.update(
            "users", ContentValues().apply { put("password_hash", newHash) },
            "id = ?", arrayOf(userId.toString())
        ) == 1
    }

    // Updates a password only after the current password has been verified.
    fun updatePassword(userId: Long, currentPassword: String, newPassword: String): Boolean {
        if (userId <= 0 || currentPassword.isBlank() || newPassword.length < 8) return false
        val storedHash = readableDatabase.query(
            "users", arrayOf("password_hash"), "id = ?", arrayOf(userId.toString()), null, null, null
        ).use { cursor ->
            if (!cursor.moveToFirst()) return false
            cursor.getString(cursor.getColumnIndexOrThrow("password_hash"))
        }
        val parts = storedHash.split(":", limit = 2)
        if (parts.size != 2 || digest(currentPassword, parts[0]) != parts[1]) return false
        val newSalt = ByteArray(16).also { SecureRandom().nextBytes(it) }
            .joinToString("") { "%02x".format(it) }
        val newHash = "$newSalt:${digest(newPassword, newSalt)}"
        return writableDatabase.update(
            "users", ContentValues().apply { put("password_hash", newHash) },
            "id = ?", arrayOf(userId.toString())
        ) == 1
    }

    fun saveRecord(userId: Long, id: Long?, date: String, steps: Int, calories: Int, water: Int): Boolean {
        if (userId <= 0 || runCatching { LocalDate.parse(date) }.isFailure || steps < 0 || calories < 0 || water < 0) return false
        val values = ContentValues().apply { put("user_id", userId); put("date", date); put("steps", steps); put("calories", calories); put("water", water) }
        return if (id == null) writableDatabase.insert("health_records", null, values) != -1L
        else writableDatabase.update("health_records", values, "id = ? AND user_id = ?", arrayOf(id.toString(), userId.toString())) > 0
    }
    fun getRecords(userId: Long, date: String = ""): List<HealthRecord> {
        val results = mutableListOf<HealthRecord>()
        val where = if (date.isBlank()) "user_id = ?" else "user_id = ? AND date = ?"
        val args = if (date.isBlank()) arrayOf(userId.toString()) else arrayOf(userId.toString(), date)
        readableDatabase.query("health_records", arrayOf("id", "user_id", "date", "steps", "calories", "water"), where, args, null, null, "date DESC, id DESC").use { c ->
            while (c.moveToNext()) results.add(HealthRecord(c.getLong(0), c.getLong(1), c.getString(2), c.getInt(3), c.getInt(4), c.getInt(5)))
        }
        return results
    }
    fun deleteRecord(userId: Long, recordId: Long): Boolean = writableDatabase.delete("health_records", "id = ? AND user_id = ?", arrayOf(recordId.toString(), userId.toString())) > 0
    fun seedDemoRecords(userId: Long) {
        if (getRecords(userId).isNotEmpty()) return
        val today = LocalDate.now()
        listOf(Triple(7850, 420, 2000), Triple(6200, 350, 1800), Triple(9100, 510, 2400)).forEachIndexed { index, values ->
            saveRecord(userId, null, today.minusDays(index.toLong()).toString(), values.first, values.second, values.third)
        }
    }
}
