package com.cs407.snapnourish

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Member2Firestore {

    private val db = FirebaseFirestore.getInstance() // initialize Firestore
    private val auth = FirebaseAuth.getInstance() // initialize FirebaseAuth

    fun saveTesterMealData(){
        // retrieve current user id, if no user is found, defaults to "testUser"
        val userId = auth.currentUser?.uid ?: "testuser"
        // creates key-value map of meal data store
        val mealData = hashMapOf(
            "userId" to userId,
            "mealName" to "meal tester",
            "calories" to 300,
            "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )

        db.collection("meals").add(mealData).addOnSuccessListener { documentReference ->
            Log.d("Member2Firestore", "Meal data added with ID: ${documentReference.id}")
        }
            .addOnFailureListener{ e ->
                Log.e("Member2Firestore", "Error adding meal data", e)
            }
    }

    fun fetchMealData() {
        // retrieve current user id, if no user is found, defaults to "testUser"
        val userId = auth.currentUser?.uid ?: "testuser"

        // selects meals collection from the firebase database,
        db.collection("meals")
            //and return meal document of userId
            .whereEqualTo("userId", userId)
            // execute query and retrieve all matching documents from Firestore
            .get()
            // triggered if the query is successful and returns a result
            .addOnSuccessListener { result ->
                // iterates over each document in the query result
                for (document in result) {
                    // replaced later with converting firebase document into object
                    // and display meals in UI
                    Log.d("Member2Firestore", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Member2Firestore", "Error getting documents", e)
        }
    }
}