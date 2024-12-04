package com.cs407.snapnourish

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cs407.snapnourish.model.Photo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HistoryActivity : AppCompatActivity() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var currentMonthTextView: TextView
    private var calendar = Calendar.getInstance()
    private lateinit var adapter: PhotoAdapter
    //data class
    data class ImageItem(
        val imageUrl: String? = null
    )

    class ImageAdapter(private val images: List<ImageItem>) :
        RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
            class ImageViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view)

        // might be parent: android.view.ViewGroup
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_history, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageItem = images[position]
            Glide.with(holder.itemView.context)
                .load(imageItem.imageUrl)
                .into(holder.itemView.imageView) //assume imageView is in layout?
        }

        override fun getItemCount(): Int = iamges.size

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        photoRecyclerView = findViewById(R.id.photoRecyclerView)
        currentMonthTextView = findViewById(R.id.currentMonth)

        adapter = PhotoAdapter(getPhotosForCurrentMonth())
        photoRecyclerView.layoutManager = GridLayoutManager(this, 2)
        photoRecyclerView.adapter = adapter

        updateMonthDisplay()

        findViewById<Button>(R.id.btn_previous_month).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
        }

        findViewById<Button>(R.id.btn_next_month).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
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

    private fun updateMonthDisplay() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        currentMonthTextView.text = dateFormat.format(calendar.time)
    }

    private fun getPhotosForCurrentMonth(): List<Photo> {
        //TODO
        val database = Firebase.firestore
        val imageList = mutableListOf<ImageModel>()
        val collection = database.collection("recipe_photos")
        collection.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    //Log.d(TAG, "${document.id} => ${document.data}")
                    //val items = querySnapshot.toObjects(Item::class.java)
                    val imageUrl = document.getString("imageURL")
                    if (imageUrl != null) {
                        imageList.add(ImageModel(imageUrl))
                    }
                }
                adapter.updatePhotos(imageList)
            }
        return emptyList()
    }
}

