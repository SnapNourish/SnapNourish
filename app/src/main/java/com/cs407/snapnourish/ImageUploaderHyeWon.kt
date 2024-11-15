package com.cs407.snapnourish

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ImageUploaderHyeWon {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Function to upload photo in Firebase Storage and save its url
    fun uploadPhotoAndSaveUrl(fileUri: Uri, onComplete: (Boolean) -> Unit) {
        // generate unique filename for each image
        val fileName = "images/${UUID.randomUUID()}.jpg"
        // reference to the file path in Firebase Storage
        val storageRef = storage.reference.child(fileName)

        // upload fileUri (image file) to Firebase Storage
        storageRef.putFile(fileUri).addOnSuccessListener {
            // if upload is successful, get the download URL
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                // save the download URL in Firebase
                savePhotoUrlToStorage(downloadUri.toString(), onComplete)
            }
        }
            .addOnFailureListener{ e->
                Log.e("StoreImage", "Photo upload failed: ${e.message}")
                onComplete(false);
            }
    }

    // Function to save the photo's download URL in Firebase
    private fun savePhotoUrlToStorage(downloadUrl: String, onComplete: (Boolean) -> Unit){
        // Data to be saved in Firestore, including URL and timestamp
        val photoData = hashMapOf(
            "photoUrl" to downloadUrl,
            "timestmap" to System.currentTimeMillis()
        )

        // add the photo data to the "photos" collection in Firestore
        db.collection("photos")
            .add(photoData)
            .addOnSuccessListener{ documentReference ->
                Log.d("StoreImage", "Photo URL saved with ID: ${documentReference.id}")
                onComplete(true)
            }
            .addOnFailureListener{ e->
                Log.e("StoreImage", "Error saving photo URL", e)
                onComplete(false)
            }
    }

}