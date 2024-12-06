package com.cs407.snapnourish

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
import okhttp3.Response
import org.json.JSONObject

// This class contains code used to analyse the nutritional content in an image
class ImageScanning : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_image_scanning)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        generateImageResponse()
    }

    private fun generateImageResponse() {
        // for image scanning
        generateAccessToken { accessToken ->
            val client = OkHttpClient()
            Log.d("debugTag", "client set")

    //            https://storage.googleapis.com/generativeai-downloads/images/scones.jpg  file uri to use for testing in json
            // TODO change fileUri for json
            // JSON payload with image and text prompt
            val json = """
            {
                "contents": {
                    "role": "user",
                    "parts": [
                        {
                            "fileData": {
                                "mimeType": "image/jpeg",
                                "fileUri": "gs://snapnourish-3028a.firebasestorage.app/images/burger.jpg"
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
    //                .url("https://us-central1-aiplatform.googleapis.com/v1/projects/snapnourish-440719/locations/us-central1/publishers/google/models/gemini-1.5-flash:generateContent")
                .url("https://us-central1-aiplatform.googleapis.com/v1/projects/snapnourish-3028a/locations/us-central1/publishers/google/models/gemini-1.5-flash:generateContent")
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
                        val responseToParse = responseText.trimIndent()
//                        Log.d("VertexAI Response To Image:", responseText)
                        // Parse the response and display text:
                        val toDisplay: String = parseJSON(responseToParse)
                        Log.d("AI Response:", toDisplay)

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
//                val inputStream: InputStream = resources.openRawResource(R.raw.snapnourish_440719_key)
                val inputStream: InputStream = resources.openRawResource(R.raw.snapnourish_3028a_vertexai_key)
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

    fun parseJSON(responseString: String): String {
        val jsonObject = JSONObject(responseString)

        // Navigate to "candidates" array
        val candidatesArray = jsonObject.getJSONArray("candidates")

        // Get the first candidate object
        val firstCandidate = candidatesArray.getJSONObject(0)

        // Navigate to "content" -> "parts" array
        val contentObject = firstCandidate.getJSONObject("content")
        val partsArray = contentObject.getJSONArray("parts")

        // Get the first part and its "text"
        val firstPart = partsArray.getJSONObject(0)
        val text = firstPart.getString("text")

        // Print the extracted text
//        Log.d("Extracted Text: ", text)
        return text
    }

}


