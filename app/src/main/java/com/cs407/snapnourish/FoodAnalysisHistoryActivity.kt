package com.cs407.snapnourish

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FoodAnalysisHistoryActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_analysis_history)

        // Retrieve data from Firestore
        db.collection("nutrition_photos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val photoUrl = document.getString("photoUrl")
                    val timestamp = document.getLong("timestamp")
                    if (photoUrl != null && timestamp != null) {
                        displayPhoto(photoUrl, timestamp) // Display photo from the retrieved URL
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FoodAnalysisHistory", "Error loading nutrition photos", exception)
                Toast.makeText(this, "Failed to load nutrition photos", Toast.LENGTH_SHORT).show()
            }
    }


    private fun displayPhoto(photoUrl: String, timestamp: Long) {
    }
}
