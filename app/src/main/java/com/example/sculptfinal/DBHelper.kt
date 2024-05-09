package com.example.sculptfinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserManager.db"
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_CURRENT_WEIGHT = "currentWeight"
        private const val COLUMN_DESIRED_WEIGHT = "desiredWeight"
        private const val COLUMN_BIRTH_DATE = "birthDate"
        private const val COLUMN_LOSE_WEIGHT = "wantsToLoseWeight"
        private const val COLUMN_GAIN_MUSCLE = "wantsToGainMuscle"

        // Constants for the calories table
        private const val TABLE_CALORIES = "calories"
        private const val COLUMN_CALORIES_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_CALORIES_DATE = "date"
        private const val COLUMN_CALORIES_AMOUNT = "amount"
    }

    //data classes
    data class User(
        val name: String,
        val password: String,
        val currentWeight: Int,
        val desiredWeight: Float,
        val birthDate: String,
        val wantsToLoseWeight: Boolean,
        val wantsToGainMuscle: Boolean
    )

    data class CalorieEntry(
        val id: Int,
        val userId: Int,
        val date: String,
        val calories: Int
    )

    //Tables
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_USERS = """
    CREATE TABLE IF NOT EXISTS $TABLE_USERS (
        $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_NAME TEXT NOT NULL,
        $COLUMN_PASSWORD TEXT NOT NULL,
        $COLUMN_CURRENT_WEIGHT INTEGER,
        $COLUMN_DESIRED_WEIGHT REAL,
        $COLUMN_BIRTH_DATE TEXT,
        $COLUMN_LOSE_WEIGHT INTEGER DEFAULT 0,
        $COLUMN_GAIN_MUSCLE INTEGER DEFAULT 0
    )
    """.trimIndent()

        val CREATE_TABLE_CALORIES = """
    CREATE TABLE IF NOT EXISTS $TABLE_CALORIES (
        $COLUMN_CALORIES_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $COLUMN_USER_ID INTEGER NOT NULL,
        $COLUMN_CALORIES_DATE TEXT NOT NULL,
        $COLUMN_CALORIES_AMOUNT INTEGER NOT NULL,
        FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID) ON DELETE CASCADE
    )
    """.trimIndent()

        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_CALORIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALORIES")
        onCreate(db)
    }

    //register the new user in database
    fun addUser(name: String, password: String, currentWeight: Int, desiredWeight: Float, birthDate: String, wantsToLoseWeight: Boolean, wantsToGainMuscle: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CURRENT_WEIGHT, currentWeight)
            put(COLUMN_DESIRED_WEIGHT, desiredWeight)
            put(COLUMN_BIRTH_DATE, birthDate)
            put(COLUMN_LOSE_WEIGHT, if (wantsToLoseWeight) 1 else 0)
            put(COLUMN_GAIN_MUSCLE, if (wantsToGainMuscle) 1 else 0)
        }
        val userId = db.insert(TABLE_USERS, null, values)
        if (userId != -1L) {
            // Insert initial calorie entry
            val calorieValues = ContentValues().apply {
                put(COLUMN_USER_ID, userId)
                put(COLUMN_CALORIES_DATE, getCurrentDate())
                put(COLUMN_CALORIES_AMOUNT, 0)
            }
            db.insert(TABLE_CALORIES, null, calorieValues)
        }
        db.close()
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getUserByUsernameAndPassword(username: String, password: String): User? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_NAME = ? AND $COLUMN_PASSWORD = ?"
        db.rawQuery(query, arrayOf(username, password)).use { cursor ->
            if (cursor.moveToFirst()) {
                return User(
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    currentWeight = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_WEIGHT)),
                    desiredWeight = cursor.getFloat(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_DESIRED_WEIGHT
                        )
                    ),
                    birthDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTH_DATE)),
                    wantsToLoseWeight = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_LOSE_WEIGHT
                        )
                    ) == 1,
                    wantsToGainMuscle = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_GAIN_MUSCLE
                        )
                    ) == 1
                )
            }
        }
        return null
    }

    fun addCalorieEntry(userId: Int, date: String, calories: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_CALORIES_DATE, date)
            put(COLUMN_CALORIES_AMOUNT, calories)
        }
        db.insert(TABLE_CALORIES, null, values)
        db.close()
    }

fun getUserCalories(userId: Int): Int {
    val db = this.readableDatabase
    val cursor = db.rawQuery("SELECT SUM(calories) FROM $TABLE_CALORIES WHERE user_id = ?", arrayOf(userId.toString()))
    if (cursor.moveToFirst()) {
        val totalCalories = cursor.getInt(0)
        cursor.close()
        db.close()
        return totalCalories
    }
    cursor.close()
    db.close()
    return 0
}


    //total calories
    fun getTotalCalories(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM($COLUMN_CALORIES_AMOUNT) FROM $TABLE_CALORIES", null)
        var totalCalories = 0
        if (cursor.moveToFirst()) {
            totalCalories = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return totalCalories
    }

    //update the calories
    fun updateCalorieEntry(id: Int, newCalories: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CALORIES_AMOUNT, newCalories)

        try {
            val rowsAffected = db.update(TABLE_CALORIES, values, "$COLUMN_CALORIES_ID = ?", arrayOf(id.toString()))

            if (rowsAffected == 0) {
                Log.e("DBHelper", "No rows updated, check your entry ID and table configuration")
            } else {
                Log.d("DBHelper", "Updated successfully $rowsAffected row(s)")
            }
        } catch (e: Exception) {
            Log.e("DBHelper", "Error updating entry", e)
        } finally {
            db.close()
        }
    }
    //delete calories for modify activity
    fun deleteCalorieEntry(id: Int) {
        val db = this.writableDatabase
        val deleteCount = db.delete(TABLE_CALORIES, "$COLUMN_CALORIES_ID = ?", arrayOf(id.toString()))
        if (deleteCount == 0) {
            Log.e("DBHelper", "Failed to delete entry with ID: $id")
        } else {
            Log.d("DBHelper", "Successfully deleted $deleteCount entry(s)")
        }
        db.close()
    }
    fun getAllCalorieEntries(): List<CalorieEntry> {
        val entries = mutableListOf<CalorieEntry>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_CALORIES,
            null,
            null,
            null,
            null,
            null,
            null
        )
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES_ID))
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CALORIES_DATE))
            val calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES_AMOUNT))

            entries.add(CalorieEntry(id, userId, date, calories))
        }

        cursor.close()
        db.close()

        return entries
    }


}
