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

    /**
     * This function gets the user's email and password to login. If registered and entered correctly,
     * user will be sent to the main menu screen. On failure, a message will appear.
     */
    fun login(view: View) {
        val userEmailIn = findViewById<EditText>(R.id.emailInput)
        val userEmail = userEmailIn.text.toString()
        val userPasswordIn = findViewById<EditText>(R.id.passwordInput)
        val userPassword = userPasswordIn.text.toString()
        auth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                //TODO: Go to main screen on successful login
                //val intent = Intent(this)
                //startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * This function will send the user to the registration page if they press the button.
     */
    fun goToSignUp(view: View) {
        //val intent = Intent(this)
        //startActivity(this)
    }

}