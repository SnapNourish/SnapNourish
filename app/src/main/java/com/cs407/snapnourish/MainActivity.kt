package com.cs407.snapnourish

import android.content.Intent
import com.cs407.snapnourish.R
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Move to Home.kt Page
        findViewById<Button>(R.id.loginButton).setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        // Move to SignUp.kt Page
        findViewById<Button>(R.id.signupButton).setOnClickListener {
            val intent = Intent(this, SignUpGurleen::class.java)
            startActivity(intent)
        }
    }

}