package com.example.healthmate


class HealthRepository(
    private val database: HealthMateDatabase
) {
    // Gets the user's profile details and goals from the database.
    fun profile(userId: Long) = database.getProfile(userId)

    // Saves the user's profile details in the database and also used for Update details.
    fun saveProfile(userId: Long, profile: UserProfile) = database.saveProfile(userId, profile)

    // Get the user's health records with date
    fun records(userId: Long, date: String = "") = database.getRecords(userId, date)

    // Save or update a new health record an existing record
    fun save(userId: Long,
             id: Long?, date: String, steps: Int, calories: Int, water: Int) = database.saveRecord(userId, id, date, steps, calories, water)

    // Delete a selected health record
    fun delete(userId: Long, id: Long) = database.deleteRecord(userId, id)

    // Retrieve the user's wellness records from a selected table
    fun wellness(table: String, userId: Long) = database.wellnessLogs(table, userId)

    // Save or update a new wellness entry an existing one
    fun saveWellness(table: String, userId: Long, id: Long?, fields: Map<String, String>) = database.saveWellness(table, userId, id, fields)

    // Delete a selected wellness entry
    fun deleteWellness(table: String, userId: Long, id: Long) = database.deleteWellness(table, userId, id)

    // Reset a password from the Forgot Password screen using the registered email.
    fun resetPassword(email: String, newPassword: String) =
        database.resetPassword(email, newPassword)

    // Add sample health records for demonstration purposes
    fun seed(userId: Long) = database.seedDemoRecords(userId)
}
