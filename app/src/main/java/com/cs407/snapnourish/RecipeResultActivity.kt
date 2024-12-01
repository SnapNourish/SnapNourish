package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeResultActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_result)

        val resultImageView = findViewById<ImageView>(R.id.resultImageView)
        val recipeTitleTextView = findViewById<TextView>(R.id.RecipeTitleTextView)
        val nutritionInfoTextView = findViewById<TextView>(R.id.nutritionInfoTextView)

        // Firestore
        db.collection("recipe_photos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val photoUrl = document.getString("photoUrl")
                    val recipe = document.getString("recipe") ?: "No recipe information available."
                    val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()

                    if (photoUrl != null) {
                        displayPhoto(photoUrl, recipe, timestamp, resultImageView, recipeTitleTextView, nutritionInfoTextView)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load recipe photos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayPhoto(
        photoUrl: String,
        recipe: String,
        timestamp: Long,
        resultImageView: ImageView,
        recipeTitleTextView: TextView,
        nutritionInfoTextView: TextView
    ) {
        // image
        Glide.with(this).load(Uri.parse(photoUrl)).into(resultImageView)

        // Recipe title
        recipeTitleTextView.text = "Recipe"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedTimestamp = dateFormat.format(Date(timestamp))

        // Recipe
        nutritionInfoTextView.text = """
            $recipe
        """.trimIndent()














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
