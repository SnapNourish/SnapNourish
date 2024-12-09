package com.cs407.snapnourish

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class Chatbot : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatbot)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Call generateContent (plain Gemini model for chatbot)
        generateContentInCoroutine()
    }


    fun generateContentInCoroutine() {
        // Create your GenerativeModel instance
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        // Define your prompt
        val prompt = "How many calories are in 1 scoop of chocolate ice cream?"

        // Use lifecycleScope to call generateContent in a coroutine
        lifecycleScope.launch {
            try {
                // Call the suspend function generateContent
                val response = generativeModel.generateContent(prompt)
                // Use the response
                println(response.text)
            } catch (e: Exception) {
                // Handle any errors
                println("Error generating content: ${e.message}")
            }
        }
    }

}