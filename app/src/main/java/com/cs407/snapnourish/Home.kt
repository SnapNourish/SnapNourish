package com.cs407.snapnourish

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class Home : AppCompatActivity() {

    private val cameraPermission = Manifest.permission.CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

        // to CameraX
        findViewById<Button>(R.id.btn_scan_nutrition).setOnClickListener {
            checkAndRequestCameraPermission {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("isRecipe", false)
                startActivity(intent)
            }
        }

        // to CameraX
        findViewById<Button>(R.id.btn_scan_meal).setOnClickListener {
            checkAndRequestCameraPermission {
                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("isRecipe", true)
                startActivity(intent)
            }
        }
    }

    private fun checkAndRequestCameraPermission(onPermissionGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            requestPermissionLauncher.launch(cameraPermission)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Get Camera Permission", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Need Camera Permission", Toast.LENGTH_SHORT).show()
        }
    }

    // This function returns nutritional information for a scanned meal/dish.
    private fun generateNutritionalInfo(fileUri: String) {
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
                                "fileUri": "$fileUri"
                            }
                        },
                        {
                            "text": "I am creating an app that analyses the dish in an image and
                             outputs the nutritional information for it in 1 serving of that dish. 
                             In this image, identify the dish shown and then output its name and nutritional 
                             information in a clean and easy way so I can parse the data and display
                              it to a table in my app. Structure the response with the following: 
                              name of dish, number of calories in 1 typical serving, followed by 
                              nutritional information (with units in mg or g). The nutritional information
                              should include quantities for carbohydrates, protein, saturated fat, unsaturated fat,
                              and fiber in this exact order. Include these titles and their respective
                              units in brackets next to that title. Here is an example of the the exact format I want the nutritional information in:
                              name of dish,calories,carbohydrates [g],protein [g],saturated fat [g],unsaturated fat [g],fiber [g],
                              Apple Pie,300,30,20,8,12,2. Do not include any other information as I 
                              need only the data to be present so I can easily parse and display it.
                              Format the data in a csv file."
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

    // This function returns meal recommendations for scanned ingredients.
    private fun generateMealRecommendations(fileUri: String) {
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
                                "fileUri": "$fileUri"
                            }
                        },
                        {
                            "text": "I am creating an app that analyses the ingredients in an image 
                             and outputs meal recommendations using the ingredients in the image. 
                             In this image, identify all the ingredients and then output 3 meal
                             recommendations using these ingredients. Structure the response with 
                             the following: name of the dish and approximate cooking time for 1
                             serving of this dish. Here is an example of the the exact format I want the
                             response in: name of dish, cook time of dish, Chicken caesar salad,
                             15 minutes. Do not include any other information as I need only the
                             data to be present so I can easily parse and display it.
                             Format the data in a csv file."

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

    // Function to parse the response from AI model and extract the necessary details.
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
