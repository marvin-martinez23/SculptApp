package com.example.sculptfinal

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sculptfinal.R.layout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddCaloriesActivity : AppCompatActivity() {
    private lateinit var caloriesEditText: EditText
    private lateinit var saveCaloriesButton: ImageButton
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_add_calories)
        // Initialize DBHelper
        dbHelper = DBHelper(this)
        caloriesEditText = findViewById(R.id.caloriesEditText)
        saveCaloriesButton = findViewById(R.id.saveCaloriesButton)
        val backButton: ImageButton = findViewById(R.id.backButton)


        saveCaloriesButton.setOnClickListener {
            saveCaloriesToDatabase()
        }

        backButton.setOnClickListener {
            //return to dashboard
            finish()
        }
    }

    //functions for buttons
    private fun saveCaloriesToDatabase() {
        val caloriesString = caloriesEditText.text.toString()
        if (caloriesString.isNotEmpty()) {
            val calories = caloriesString.toInt()
            val userId = fetchUserId()
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            //call method from DBHelper
            dbHelper.addCalorieEntry(userId, currentDate, calories)

            //update the calories in the text views
            updateCalorieDisplay()
            Toast.makeText(this, "Calories saved!", Toast.LENGTH_SHORT).show()

            // return to the dashboard
            finish()
        } else {
            Toast.makeText(this, "Please enter the number of calories", Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchUserId(): Int {
        val sharedPrefs = this.getSharedPreferences("your_preferences_name", Context.MODE_PRIVATE)
        return sharedPrefs.getInt("userId", 0)
    }

    private fun updateCalorieDisplay() {
        val totalCalories = dbHelper.getTotalCalories()
        val totalCaloriesTextView: TextView = findViewById(R.id.totalCaloriesTextView)
        totalCaloriesTextView.text = "Total Calories: $totalCalories"
    }

    override fun onResume() {
        super.onResume()
        updateCalorieDisplay()
    }


}