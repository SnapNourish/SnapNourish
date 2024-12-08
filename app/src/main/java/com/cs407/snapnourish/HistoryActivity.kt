package com.cs407.snapnourish

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cs407.snapnourish.model.Photo
import androidx.recyclerview.widget.DividerItemDecoration
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryActivity : AppCompatActivity() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var currentMonthTextView: TextView
    private var calendar = Calendar.getInstance()
    private lateinit var adapter: PhotoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

        val itemDecoration = SpaceItemDecoration(16) // 16dp 간격
        photoRecyclerView.addItemDecoration(itemDecoration)

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

        val updatedPhotos = getPhotosForCurrentMonth()
        adapter = PhotoAdapter(updatedPhotos)
        photoRecyclerView.adapter = adapter
    }

    private fun getPhotosForCurrentMonth(): List<Photo> {
        val currentMonth = calendar.get(Calendar.MONTH)

        return if (currentMonth == Calendar.DECEMBER) {
            listOf(
                Photo(R.drawable.food1, "2024-12-1"),
                Photo(R.drawable.food2, "2024-12-2"),
                Photo(R.drawable.food3, "2024-12-3")
            )
        } else {
            emptyList()
        }
    }
}

