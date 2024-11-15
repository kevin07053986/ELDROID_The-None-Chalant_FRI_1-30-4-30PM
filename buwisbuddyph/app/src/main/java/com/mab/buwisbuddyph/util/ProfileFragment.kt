package com.mab.buwisbuddyph.util

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mab.buwisbuddyph.R

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var userProfileImg: ImageView
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private var oldImageUri: String? = null

    companion object {
        private const val TAG = "ProfileFragment"
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        userProfileImg = view.findViewById(R.id.userProfileImg)
        userProfileImg.setOnClickListener {
            if (isStoragePermissionGranted()) {
                openGallery()
            }
        }

        val currentUser = auth.currentUser
        currentUser?.let {
            loadUserProfile(it.uid)
        }

        val editButton = view.findViewById<Button>(R.id.editButton)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        editButton.setOnClickListener {
            setEditable(true)
            saveButton.visibility = Button.VISIBLE
        }

        saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun setEditable(isEditable: Boolean) {
        view?.findViewById<EditText>(R.id.nameTV)?.isEnabled = isEditable
        view?.findViewById<EditText>(R.id.numberTV)?.isEnabled = isEditable
        view?.findViewById<EditText>(R.id.genderTV)?.isEnabled = isEditable
        view?.findViewById<EditText>(R.id.tinTV)?.isEnabled = isEditable
        view?.findViewById<EditText>(R.id.emailTV)?.isEnabled = isEditable
        Log.d("Profile Edit", "Set all fields to editable: $isEditable")
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
                    val profileImage = document.getString("userProfileImage")

                    oldImageUri = profileImage // Store the old image URI if it exists

                    view?.findViewById<EditText>(R.id.nameTV)?.setText(name)
                    view?.findViewById<EditText>(R.id.numberTV)?.setText(number)
                    view?.findViewById<EditText>(R.id.genderTV)?.setText(gender)
                    view?.findViewById<EditText>(R.id.tinTV)?.setText(tin)
                    view?.findViewById<EditText>(R.id.emailTV)?.setText(email)

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

        val name = view?.findViewById<EditText>(R.id.nameTV)?.text.toString()
        val number = view?.findViewById<EditText>(R.id.numberTV)?.text.toString()
        val gender = view?.findViewById<EditText>(R.id.genderTV)?.text.toString()
        val tin = view?.findViewById<EditText>(R.id.tinTV)?.text.toString()
        val email = view?.findViewById<EditText>(R.id.emailTV)?.text.toString()

        imageUri?.let { newImageUri ->
            val imageRef = storageRef.child("userProfileImages/${uid}_profile_image")
            val uploadTask = imageRef.putFile(newImageUri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) task.exception?.let { throw it }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    val userUpdates = mapOf(
                        "userFullName" to name,
                        "userNumber" to number,
                        "userGender" to gender,
                        "userTin" to tin,
                        "userEmail" to email,
                        "userProfileImage" to downloadUri.toString()
                    )

                    db.collection("users").document(uid)
                        .update(userUpdates)
                        .addOnSuccessListener {
                            Log.d(TAG, "User profile updated successfully")
                            setEditable(false)
                            view?.findViewById<Button>(R.id.saveButton)?.visibility = Button.GONE
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
                    view?.findViewById<Button>(R.id.saveButton)?.visibility = Button.GONE
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error updating user profile", exception)
                }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            userProfileImg.setImageURI(uri)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        }
    }
}
