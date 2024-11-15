package com.cs407.snapnourish

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
//import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
//import com.google.cloud.vertexai.VertexAI
//import com.google.cloud.vertexai.api.GenerateContentResponse
//import com.google.cloud.vertexai.generativeai.GenerativeModel
//import com.google.cloud.vertexai.generativeai.ResponseHandler
//import org.apache.http.client.ResponseHandler
import java.io.IOException
import kotlin.concurrent.thread
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var credentials: GoogleCredentials

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Call the function when the activity starts
//        sendTextPrompt()

//        accessVertex() chatGPT code
        // Chatbot button
        val chatbotButton = findViewById<Button>(R.id.buttonChatbot)
        // Set click listener on the button
        chatbotButton.setOnClickListener {
            // Intent to navigate to Chatbot Activity
            val intent = Intent(this, Chatbot::class.java)
            startActivity(intent)
        }

        // Image scanner button
        val imageScannerButton = findViewById<Button>(R.id.buttonImageScanner)
        // Set click listener on the button
        imageScannerButton.setOnClickListener {
            // Intent to navigate to Chatbot Activity
            val intent = Intent(this, ImageScanning::class.java)
            startActivity(intent)
        }

        // For text-to-text use cases
//        generateAccessToken { accessToken ->
//            // Now you can use the accessToken in your HTTP request
//            val client = OkHttpClient()
//
//            val json = """
//            {
//              "contents": {
//                "role": "user",
//                "parts": [
//                  {
//                    "text": "What\'s a good name for a flower shop that specializes in selling bouquets of dried flowers?"
//                  }
//                ]
//              }
//            }
//              """.trimIndent()
//
//            val requestBody = json.toRequestBody("application/json".toMediaType())
//
//            val request = Request.Builder()
//                .url("https://us-central1-aiplatform.googleapis.com/v1/projects/snapnourish-440719/locations/us-central1/publishers/google/models/gemini-1.5-flash:generateContent")
//                .addHeader("Authorization", "Bearer $accessToken")
//                .addHeader("Content-Type", "application/json")
//                .post(requestBody)
//                .build()
//
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    response.body?.let {
//                        val responseText = it.string()
//                        try {
//                            // Parse the JSON response
//                            val responseJson = JSONObject(responseText)
//                            val candidatesArray = responseJson.getJSONArray("candidates")
//                            val firstCandidate = candidatesArray.getJSONObject(0)
//                            val content = firstCandidate.getJSONObject("content")
//                            val partsArray = content.getJSONArray("parts")
//                            val firstPart = partsArray.getJSONObject(0)
//                            val textResponse = firstPart.getString("text")
//
//                            // Log the extracted text
//                            Log.d("VertexAI Response Text", textResponse)
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                    }
//                }
//            })
//        }

        // for image scanning
        generateAccessToken { accessToken ->
            val client = OkHttpClient()

            // JSON payload with image and text prompt
            val json = """
        {
            "contents": {
                "role": "user",
                "parts": [
                    {
                        "fileData": {
                            "mimeType": "image/jpeg",
                            "fileUri": "https://storage.googleapis.com/generativeai-downloads/images/scones.jpg"
                        }
                    },
                    {
                        "text": "What dish is in this picture?"
                    }
                ]
            }
        }
    """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://us-central1-aiplatform.googleapis.com/v1/projects/snapnourish-440719/locations/us-central1/publishers/google/models/gemini-1.5-flash:generateContent")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let {
                        val responseText = it.string()
                            Log.d("VertexAI Response Text", responseText)
                    }
                }
            })
        }
    }

    // Function to generate access token
    private fun generateAccessToken(onTokenReceived: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load the JSON key from resources
                val inputStream: InputStream = resources.openRawResource(R.raw.snapnourish_440719_key)
                Log.d("debugTag", "input stream set")

                // Use GoogleCredentials to authenticate with the JSON key
                val credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                Log.d("debugTag", "credentials set")

                // Refresh the token in a background thread
                credentials.refreshIfExpired()
                Log.d("debugTag", "credentials refreshed")

                val accessToken = credentials.accessToken.tokenValue
                Log.d("debugTag", "access token set")

                // Switch to the main thread to pass the token back
                withContext(Dispatchers.Main) {
                    onTokenReceived(accessToken)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}