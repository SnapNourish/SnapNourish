package com.cs407.snapnourish

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //    insets
        //}
        //var auth = FirebaseAuth.getInstance()
    }
    var auth = FirebaseAuth.getInstance()
    fun login(view: View) {
        val userEmailIn = findViewById<EditText>(R.id.emailInput)
        val userEmail = userEmailIn.text.toString()
        val userPasswordIn = findViewById<EditText>(R.id.passwordInput)
        val userPassword = userPasswordIn.text.toString()
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                //val intent = Intent(this)
            }
        }
    }

}