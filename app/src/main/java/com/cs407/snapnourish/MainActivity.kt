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

//        generateTextResponse()

//        generateImageResponse()
    }

    private fun generateTextResponse() {
        // For text-to-text use cases
        generateAccessToken { accessToken ->
            // Now you can use the accessToken in your HTTP request
            val client = OkHttpClient()

            val json = """
            {
              "contents": {
                "role": "user",
                "parts": [
                  {
                    "text": "What\'s a good name for a flower shop that specializes in selling bouquets of dried flowers?"
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
                        try {
                            // Parse the JSON response
                            val responseJson = JSONObject(responseText)
                            val candidatesArray = responseJson.getJSONArray("candidates")
                            val firstCandidate = candidatesArray.getJSONObject(0)
                            val content = firstCandidate.getJSONObject("content")
                            val partsArray = content.getJSONArray("parts")
                            val firstPart = partsArray.getJSONObject(0)
                            val textResponse = firstPart.getString("text")

                            // Log the extracted text
                            Log.d("VertexAI Response Text", textResponse)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            })
        }
    }

    private fun generateImageResponse() {
        // for image scanning
        generateAccessToken { accessToken ->
            val client = OkHttpClient()
            Log.d("debugTag", "client set")

//            https://storage.googleapis.com/generativeai-downloads/images/scones.jpg  file uri to use for testing in json
            // JSON payload with image and text prompt
            val json = """
        {
            "contents": {
                "role": "user",
                "parts": [
                    {
                        "fileData": {
                            "mimeType": "image/jpeg",
                            "fileUri": "gs://snapnourish/images/burger.jpg"
                        }
                    },
                    {
                        "text": "What dish is in this picture and how many calories are in it? Give me the nutritional information of this."
                    }
                ]
            }
        }
    """.trimIndent()
            Log.d("debugTag", "json string set")
            val requestBody = json.toRequestBody("application/json".toMediaType())
            Log.d("debugTag", "json request body set")
            val request = Request.Builder()
                .url("https://us-central1-aiplatform.googleapis.com/v1/projects/snapnourish-440719/locations/us-central1/publishers/google/models/gemini-1.5-flash:generateContent")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            Log.d("debugTag", "json request sent.")
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let {
                        val responseText = it.string()
                        Log.d("VertexAI Response To Image:", responseText)
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
//                Log.d("debugTag", "input stream set")

                // Use GoogleCredentials to authenticate with the JSON key
                val credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
//                Log.d("debugTag", "credentials set")

                // Refresh the token in a background thread
                credentials.refreshIfExpired()
//                Log.d("debugTag", "credentials refreshed")

                val accessToken = credentials.accessToken.tokenValue
//                Log.d("debugTag", "access token set")

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