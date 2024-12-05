package com.cs407.snapnourish

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RecipeHistoryActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_history)

        db.collection("recipe_photos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val photoUrl = document.getString("photoUrl")
                    val timestamp = document.getLong("timestamp")
                    if (photoUrl != null && timestamp != null) {
                        Log.d("RecipeHistory", "Loaded photo URL: $photoUrl")
                        displayPhoto(photoUrl, timestamp)  // Display the retrieved URL on the screen
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("RecipeHistory", "Error loading recipe photos", exception)
                Toast.makeText(this, "Failed to load recipe photos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayPhoto(photoUrl: String, timestamp: Long) {
        /*
         * val imageView = ImageView(this)
         * Glide.with(this).load(photoUrl).into(imageView)
         * container.addView(imageView)
         *
         * adapter.notifyDataSetChanged()
         */
    }
}
