package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutritionResultActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_result)

        val resultImageView = findViewById<ImageView>(R.id.resultImageView)
        val foodNameTextView = findViewById<TextView>(R.id.foodNameTextView)
        val timestampTextView = findViewById<TextView>(R.id.timestampTextView)
        val nutritionInfoTextView = findViewById<TextView>(R.id.nutritionInfoTextView)
        val missingNutrientsTextView = findViewById<TextView>(R.id.missingNutrientsTextView)
        val recommendIngredientsTextView = findViewById<TextView>(R.id.recommendIngredientsTextView)

        val nutritionInfoDocumentId = "WOgJ7ExoqOkWgLro8hZb"
        val missingNutrientsDocumentId = "9ngNVMNGoSCd23kiIVRq"


//        val imageUri = intent.getStringExtra("IMAGE_URI")
//        val foodName = intent.getStringExtra("FOOD_NAME") ?: "Unknown Food"
//        val nutritionInfo = intent.getStringExtra("NUTRITION_INFO") ?: "No nutrition information available."
//        val timestamp = intent.getLongExtra("TIMESTAMP", System.currentTimeMillis())

//        // image load
//        imageUri?.let {
//            Glide.with(this).load(Uri.parse(it)).into(resultImageView)
//        }
//
//        // food name
//        foodNameTextView.text = foodName
//
//        // timestamp
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//        val formattedTimestamp = dateFormat.format(Date(timestamp))
//        timestampTextView.text = "Captured on: $formattedTimestamp"
//
//        // nutrition info
//        nutritionInfoTextView.text = nutritionInfo

        db.collection("nutrition_info").document(nutritionInfoDocumentId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Load image
                    val photoUrl = document.getString("photoUrl")
                    photoUrl?.let { gsUrl ->
                        loadImageFromStorage(gsUrl, resultImageView)
                    }

                    // Set food name
                    foodNameTextView.text = "Food: Burger"

                    // Set timestamp
                    val timestamp = document.getTimestamp("timestamp")?.toDate()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    timestampTextView.text = "Captured on: ${timestamp?.let { dateFormat.format(it) }}"

                    // Set nutrition info
                    val calories = document.getDouble("calories") ?: 0.0
                    val carbohydrates = document.getDouble("carbohydrates") ?: 0.0
                    val protein = document.getDouble("protein") ?: 0.0
                    val fiber = document.getDouble("fiber") ?: 0.0
                    val saturatedFat = document.getDouble("saturatedFat") ?: 0.0
                    val unsaturatedFat = document.getDouble("unsaturatedFat") ?: 0.0

                    val nutritionInfo = """
                        Calories: $calories
                        Carbohydrates: $carbohydrates g
                        Protein: $protein g
                        Fiber: $fiber g
                        Saturated Fat: $saturatedFat g
                        Unsaturated Fat: $unsaturatedFat g
                    """.trimIndent()
                    nutritionInfoTextView.text = nutritionInfo
                }
            }
            .addOnFailureListener {
                nutritionInfoTextView.text = "Failed to load nutrition information."
            }

        // Fetch missing nutrients and recommend ingredients
        db.collection("missingNutrients").document(missingNutrientsDocumentId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Set missing nutrients
                    val missingNutrients = document.get("missingNutrients") as? Map<String, Double>
                    val missingNutrientsText = missingNutrients?.entries?.joinToString("\n") { (key, value) ->
                        "$key: $value g"
                    } ?: "No missing nutrients."
                    missingNutrientsTextView.text = "Missing Nutrients:\n$missingNutrientsText"

                    // Set recommended ingredients
                    val recommendIngredients = document.get("recommendIngredients") as? List<String>
                    val recommendIngredientsText = recommendIngredients?.joinToString("\n") ?: "No recommendations available."
                    recommendIngredientsTextView.text = "Recommended Ingredients:\n$recommendIngredientsText"
                }
            }
            .addOnFailureListener {
                missingNutrientsTextView.text = "Failed to load missing nutrients."
                recommendIngredientsTextView.text = "Failed to load recommendations."
            }



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

    private fun loadImageFromStorage(gsUrl: String, imageView: ImageView) {
        val storageReference = storage.getReferenceFromUrl(gsUrl)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to load image from gs:// URL", exception)
        }
    }


}