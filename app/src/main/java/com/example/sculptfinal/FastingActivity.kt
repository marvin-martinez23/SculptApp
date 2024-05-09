package com.example.sculptfinal

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FastingActivity : AppCompatActivity() {

    private lateinit var stopwatchTextView: TextView
    private var startTime = 0L
    private var running = false
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fasting)
        val backButton: ImageButton = findViewById(R.id.backButton)
        stopwatchTextView = findViewById(R.id.fastingTimeView)
        val startButton: ImageButton = findViewById(R.id.startButton)
        val stopButton: ImageButton = findViewById(R.id.stopButton)

        startButton.setOnClickListener {
            startTime = System.currentTimeMillis()
            running = true
            handler.postDelayed(timerRunnable, 0)
        }

        stopButton.setOnClickListener {
            running = false
            handler.removeCallbacks(timerRunnable)
        }

        backButton.setOnClickListener {
            //return to dashboard
            finish()
        }
    }

    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (running) {
                val millis = System.currentTimeMillis() - startTime
                val seconds = (millis / 1000)
                val minutes = seconds / 60
                val hours = minutes / 60
                stopwatchTextView.text =
                    String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
                handler.postDelayed(this, 1000)
            }
        }
    }
}
