package com.example.sculptfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DashboardActivity : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: ImageButton
    private lateinit var totalCaloriesTextView: TextView
    private lateinit var recommendedCaloriesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        dbHelper = DBHelper(this)
        totalCaloriesTextView = findViewById(R.id.totalCaloriesTextView)
        recommendedCaloriesTextView = findViewById(R.id.recommendedCaloriesTextView)
        //buttons initialized
        button1 = findViewById(R.id.add_calories_button)
        button2 = findViewById(R.id.modify_calories_button)
        button3 = findViewById(R.id.start_fasting_button)

        setupButtons()
        updateDisplays()
    }
    override fun onResume() {
        super.onResume()
        updateDisplays()
    }
    private fun setupButtons() {
        button1.setOnClickListener {
            val intent = Intent(this@DashboardActivity, AddCaloriesActivity::class.java)
            startActivity(intent)
        }

        button2.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ModifyCaloriesActivity::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener {
            val intent = Intent(this@DashboardActivity, FastingActivity::class.java)
            startActivity(intent)
        }
    }
    private fun updateDisplays() {
        val totalCalories = dbHelper.getTotalCalories()
        totalCaloriesTextView.text = "Total Calories: $totalCalories"
    }
}