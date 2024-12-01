package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutritionResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_result)

        val resultImageView = findViewById<ImageView>(R.id.resultImageView)
        val foodNameTextView = findViewById<TextView>(R.id.foodNameTextView)
        val timestampTextView = findViewById<TextView>(R.id.timestampTextView)
        val nutritionInfoTextView = findViewById<TextView>(R.id.nutritionInfoTextView)

        val imageUri = intent.getStringExtra("IMAGE_URI")
        val foodName = intent.getStringExtra("FOOD_NAME") ?: "Unknown Food"
        val nutritionInfo = intent.getStringExtra("NUTRITION_INFO") ?: "No nutrition information available."
        val timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis())

        // image load
        imageUri?.let {
            Glide.with(this).load(Uri.parse(it)).into(resultImageView)
        }

        // food name
        foodNameTextView.text = foodName

        // timestamp
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedTimestamp = dateFormat.format(Date(timestamp))
        timestampTextView.text = "Captured on: $formattedTimestamp"

        // nutrition info
        nutritionInfoTextView.text = nutritionInfo












        // Navigate to Home screen(icon1)
        findViewById<Button>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //Navigate to History screen(icon2)
        findViewById<Button>(R.id.btn_history).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        //Navigate to Chatbot screen(icon3)
        findViewById<Button>(R.id.btn_chat).setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Settings screen(icon4)
        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}