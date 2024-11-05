package com.cs407.snapnourish

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class Member2Activity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // initialize firebase authentication
        checkUserSession() // check if user is logged in
    }

    private fun checkUserSession(){
        val currUser = auth.currentUser // get currently logged in user
        // when user is logged in
        if(currUser != null){
            Log.d("Memeber2Activity", "User logged in: ${currUser.uid}")
            val firestoreHelper = Member2Firestore()
            firestoreHelper.saveTesterMealData() // save test data
            firestoreHelper.fetchMealData() // retrieve test data
        }
        else{
            // if no user is logged in, handle the case
        }
    }



}