package com.mab.buwisbuddyph

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mab.buwisbuddyph.home.HomeActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var userProfileImg: ImageView
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private var oldImageUri: String? = null // Variable to store the old image URI

    companion object {
        private const val TAG = "ProfileActivity"
        private const val PICK_IMAGE_REQUEST = 1
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        userProfileImg = findViewById(R.id.userProfileImg)
        userProfileImg.setOnClickListener {
            if (isStoragePermissionGranted()) {
                openGallery()
            }
        }

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        val currentUser = auth.currentUser
        currentUser?.let {
            loadUserProfile(it.uid)
        }

        val editButton = findViewById<Button>(R.id.editButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        editButton.setOnClickListener {
            setEditable(true)
            saveButton.visibility = Button.VISIBLE
        }

        saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun setEditable(isEditable: Boolean) {
        findViewById<EditText>(R.id.nameTV)?.isEnabled = isEditable
        findViewById<EditText>(R.id.numberTV)?.isEnabled = isEditable
        findViewById<EditText>(R.id.genderTV)?.isEnabled = isEditable
        findViewById<EditText>(R.id.tinTV)?.isEnabled = isEditable
        findViewById<EditText>(R.id.emailTV)?.isEnabled = isEditable
        Log.d("Profile Edit", "set all to editable")
    }

    private fun loadUserProfile(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("userFullName") ?: "N/A"
                    val number = document.getString("userNumber") ?: "N/A"
                    val gender = document.getString("userGender") ?: "N/A"
                    val tin = document.getString("userTin") ?: "N/A"
                    val email = document.getString("userEmail") ?: "N/A"
                    val profileImage = document.getString("userProfileImage") // Retrieve the old image URI from Firestore

                    // Store the old image URI if it exists
                    oldImageUri = profileImage

                    findViewById<EditText>(R.id.nameTV).setText(name)
                    findViewById<EditText>(R.id.numberTV).setText(number)
                    findViewById<EditText>(R.id.genderTV).setText(gender)
                    findViewById<EditText>(R.id.tinTV).setText(tin)
                    findViewById<EditText>(R.id.emailTV).setText(email)

                    // Load the profile image using Glide
                    profileImage?.let {
                        Glide.with(this)
                            .load(it)
                            .into(userProfileImg)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun saveUserProfile() {
        val uid = auth.currentUser?.uid ?: return

        val name = findViewById<EditText>(R.id.nameTV).text.toString()
        val number = findViewById<EditText>(R.id.numberTV).text.toString()
        val gender = findViewById<EditText>(R.id.genderTV).text.toString()
        val tin = findViewById<EditText>(R.id.tinTV).text.toString()
        val email = findViewById<EditText>(R.id.emailTV).text.toString()

        // Only upload the image if a new image is selected
        imageUri?.let { newImageUri ->
            val imageRef = storageRef.child("userProfileImages/${uid}_profile_image")
            val uploadTask = imageRef.putFile(newImageUri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    // Save the download URL to Firestore
                    val userUpdates = mapOf(
                        "userFullName" to name,
                        "userNumber" to number,
                        "userGender" to gender,
                        "userTin" to tin,
                        "userEmail" to email,
                        "userProfileImage" to downloadUri.toString() // Add userProfileImage to Firestore
                    )

                    db.collection("users").document(uid)
                        .update(userUpdates)
                        .addOnSuccessListener {
                            Log.d(TAG, "User profile updated successfully")
                            // Make the EditText fields non-editable
                            setEditable(false)
                            // Hide the save button
                            findViewById<Button>(R.id.saveButton).visibility = Button.GONE
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error updating user profile", exception)
                        }
                } else {
                    Log.d(TAG, "Error uploading image: ${task.exception}")
                }
            }
        } ?: run {
            val userUpdates = mapOf(
                "userFullName" to name,
                "userNumber" to number,
                "userGender" to gender,
                "userTin" to tin,
                "userEmail" to email
            )

            db.collection("users").document(uid)
                .update(userUpdates)
                .addOnSuccessListener {
                    Log.d(TAG, "User profile updated successfully")
                    setEditable(false)
                    findViewById<Button>(R.id.saveButton).visibility = Button.GONE
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error updating user profile", exception)
                }
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            // Update the profile image here with the selected imageUri
            userProfileImg.setImageURI(imageUri)
        }
    }
}
