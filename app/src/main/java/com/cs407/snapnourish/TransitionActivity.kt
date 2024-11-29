package com.cs407.snapnourish

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TransitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition)

        val capturedImageView = findViewById<ImageView>(R.id.capturedImage)
        val viewNutritionButton = findViewById<Button>(R.id.viewNutritionButton)

        val imageUri = intent.getParcelableExtra<Uri>("IMAGE_URI")
        capturedImageView.setImageURI(imageUri)

        viewNutritionButton.setOnClickListener {
        }
    }
}
