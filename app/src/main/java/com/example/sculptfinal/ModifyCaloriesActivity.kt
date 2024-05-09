package com.example.sculptfinal

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class ModifyCaloriesActivity: AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var entries: List<DBHelper.CalorieEntry>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_calories)
        val backButton: ImageButton = findViewById(R.id.backButton)
        listView = findViewById(R.id.lvCalorieEntries)
        dbHelper = DBHelper(this)

        loadEntries()

        backButton.setOnClickListener {
            //return to dashboard
            finish()
        }

    }


    private fun loadEntries() {
        entries = dbHelper.getAllCalorieEntries()
        val entryStrings = entries.map { entry -> "Date: ${entry.date}, Calories: ${entry.calories}" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entryStrings)
        listView.adapter = adapter
        setupListViewListener()
    }

    private fun setupListViewListener() {
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedEntry = entries[position]
            showEditDeleteDialog(selectedEntry)
        }
    }
    private fun deleteEntry(entry: DBHelper.CalorieEntry) {
        dbHelper.deleteCalorieEntry(entry.id)
        loadEntries()
        updateTotalCaloriesDisplay()
        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
    }

    private fun showEditDeleteDialog(entry: DBHelper.CalorieEntry) {
        val editText = EditText(this)
        editText.setText(entry.calories.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Calories")
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val newCalories = editText.text.toString().toInt()
                updateEntry(entry.id, newCalories)
                loadEntries()
                dialog.dismiss()
            }
            .setNegativeButton("Delete") { _, _ ->
                deleteEntry(entry)
            }
            .show()
    }
    private fun updateEntry(entryId: Int, newCalories: Int) {
        dbHelper.updateCalorieEntry(entryId, newCalories)
        loadEntries()
        updateTotalCaloriesDisplay()
    }


    private fun updateTotalCaloriesDisplay() {
        val totalCalories = dbHelper.getTotalCalories()
        val totalCaloriesTextView: TextView = findViewById(R.id.totalCaloriesTextView)
        totalCaloriesTextView.text = "Total Calories: $totalCalories"
    }

    override fun onResume() {
        super.onResume()
        updateTotalCaloriesDisplay()

    }

}








