package com.example.sculptfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistrationActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextCurrentWeight: EditText
    private lateinit var editTextDesiredWeight: EditText
    private lateinit var editTextBirthDate: EditText
    private lateinit var checkBoxLoseWeight: CheckBox
    private lateinit var checkBoxGainMuscle: CheckBox
    private lateinit var submitButton: Button

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        dbHelper = DBHelper(this)
        val backButton: ImageButton = findViewById(R.id.backButton)

        editTextName = findViewById(R.id.editTextText)
        editTextPassword = findViewById(R.id.editTextTextPassword)
        editTextCurrentWeight = findViewById(R.id.editTextNumber)
        editTextDesiredWeight = findViewById(R.id.editTextNumberDecimal)
        editTextBirthDate = findViewById(R.id.editTextDate)
        checkBoxLoseWeight = findViewById(R.id.checkBox)
        checkBoxGainMuscle = findViewById(R.id.checkBox2)
        submitButton = findViewById(R.id.elevatedButton)

        backButton.setOnClickListener {
            finish()
        }
        submitButton.setOnClickListener {
            if (validateInput()) {
                saveData()
            }
            val submitButton: Button = findViewById(R.id.elevatedButton)
            submitButton.setOnClickListener {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            }
        }

    }

    private fun validateInput(): Boolean {
        if (editTextName.text.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (editTextPassword.text.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (editTextCurrentWeight.text.isEmpty()) {
            Toast.makeText(this, "Please enter your current weight", Toast.LENGTH_SHORT).show()
            return false
        }
        if (editTextDesiredWeight.text.isEmpty()) {
            Toast.makeText(this, "Please enter youre desired weight", Toast.LENGTH_SHORT).show()
            return false
        }
        if (editTextBirthDate.text.isEmpty()) {
            Toast.makeText(this, "Please enter your birth in the format DD/MM/YEAR", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun saveData() {

        val name = editTextName.text.toString()
        val password = editTextPassword.text.toString()
        val currentWeight = editTextCurrentWeight.text.toString().toIntOrNull() ?: 0
        val desiredWeight = editTextDesiredWeight.text.toString().toFloatOrNull() ?: 0f
        val birthDate = editTextBirthDate.text.toString()
        val wantsToLoseWeight = checkBoxLoseWeight.isChecked
        val wantsToGainMuscle = checkBoxGainMuscle.isChecked

        dbHelper.addUser(name, password, currentWeight, desiredWeight, birthDate, wantsToLoseWeight, wantsToGainMuscle)

        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()

    }

}

