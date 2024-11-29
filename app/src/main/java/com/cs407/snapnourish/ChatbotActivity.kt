package com.cs407.snapnourish

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatbotActivity : AppCompatActivity() {
    private lateinit var chatAdapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatbot)

        // Set edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chatbot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_chat)
        chatAdapter = ChatAdapter(chatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Handle message input and send button
        val editTextMessage = findViewById<EditText>(R.id.editText_message)
        val buttonSend = findViewById<Button>(R.id.btn_send)

        buttonSend.setOnClickListener {
            val message = editTextMessage.text.toString()
            if (message.isNotBlank()) {
                // Add user's message to the list (right side, yellow bubble)
                chatMessages.add(ChatMessage(message, true))
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)

                // Clear the input field
                editTextMessage.text.clear()

                // Simulate AI response (left side, white bubble) after a delay
                recyclerView.postDelayed({
                    chatMessages.add(ChatMessage("AI answer will be HERE", false))
                    chatAdapter.notifyItemInserted(chatMessages.size - 1)
                    recyclerView.scrollToPosition(chatMessages.size - 1)
                }, 1000) // 1-second delay
            }
        }

        // Navigate to Home screen(icon1)
        findViewById<Button>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        // Navigate to History screen(icon2)
        findViewById<Button>(R.id.btn_history).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Chatbot screen(icon3)
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
}
