package com.mab.buwisbuddyph.sign_in

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mab.buwisbuddyph.LoadingDialog
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.home.HomeActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var userProfileImg: CircleImageView
    private lateinit var userFullNameET: EditText
    private lateinit var birthDateET: EditText
    private lateinit var userEmailET: EditText
    private lateinit var userPasswordET: EditText
    private lateinit var userPasswordConfirmationET: EditText
    private lateinit var createAccountButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var spinner: Spinner
    private lateinit var loadingDialog: LoadingDialog

    companion object {
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loadingDialog = LoadingDialog(this)

        userProfileImg = findViewById(R.id.userProfileImg)
        userFullNameET = findViewById(R.id.userFullNameET)
        birthDateET = findViewById(R.id.birthDateET)
        userEmailET = findViewById(R.id.userEmailET)
        userPasswordET = findViewById(R.id.userPasswordET)
        userPasswordConfirmationET = findViewById(R.id.userPasswordConfirmationET)
        createAccountButton = findViewById(R.id.createAccountButton)
        spinner = findViewById(R.id.spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.user_type_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.setSelection((spinner.adapter as ArrayAdapter<String>).getPosition("Freelancer"))

        birthDateET.setOnClickListener {
            showDatePicker()
        }
        userProfileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageContract.launch(intent)
        }
        createAccountButton.setOnClickListener {
            createUser()
        }
    }

    private val pickImageContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            userProfileImg.setImageURI(imageUri)
            userProfileImg.tag = imageUri.toString()
        }
    }

    private fun setEditTextBackground(editText: EditText, isEmpty: Boolean) {
        if (isEmpty) {
            editText.setBackgroundResource(R.drawable.empty_fields_border)
        } else {
            editText.setBackgroundResource(R.drawable.square_border_default)
        }
    }

    private fun createUser() {
        val userFullName = userFullNameET.text.toString().trim()
        val birthDate = birthDateET.text.toString().trim()
        val userEmail = userEmailET.text.toString().trim()
        val userPassword = userPasswordET.text.toString().trim()
        val userPasswordConfirmation = userPasswordConfirmationET.text.toString().trim()
        val userAccountType = spinner.selectedItem.toString()

        setEditTextBackground(userFullNameET, userFullName.isEmpty())
        setEditTextBackground(birthDateET, birthDate.isEmpty())
        setEditTextBackground(userEmailET, userEmail.isEmpty())
        setEditTextBackground(userPasswordET, userPassword.isEmpty())
        setEditTextBackground(userPasswordConfirmationET, userPasswordConfirmation.isEmpty())

        if (userFullName.isEmpty() || birthDate.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userPasswordConfirmation.isEmpty()) {
            return
        }

        if (userPassword != userPasswordConfirmation) {
            userPasswordConfirmationET.error = "Passwords do not match"
            return
        }

        loadingDialog.loginLoadingDialog()

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val imageUri = userProfileImg.tag as? String
                        val defaultImageName = "default_profile_image.png"
                        val userProfileImage = imageUri ?: defaultImageName

                        val userInformation = hashMapOf(
                            "userID" to user.uid,
                            "userFullName" to userFullName.lowercase(Locale.ROOT),
                            "birthdate" to birthDate,
                            "userEmail" to userEmail.lowercase(Locale.ROOT),
                            "userProfileImage" to userProfileImage,
                            "createDate" to Timestamp.now() as Any,
                            "userAccountType" to userAccountType
                        )

                        if (imageUri != null) {
                            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${UUID.randomUUID()}")
                            storageRef.putFile(Uri.parse(imageUri))
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                        userInformation["userProfileImage"] = downloadUri.toString()
                                        saveUserToFirestore(userInformation)
                                        loadingDialog.dismissDialog()
                                        navigateToHome()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(TAG, "Error uploading image: ${exception.message}")
                                    saveUserToFirestore(userInformation)
                                    loadingDialog.dismissDialog()
                                    navigateToHome()
                                }
                        } else {
                            saveUserToFirestore(userInformation)
                            loadingDialog.dismissDialog()
                            navigateToHome()
                        }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismissDialog()
                }
            }
    }

    private fun saveUserToFirestore(user: HashMap<String, Any>) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .set(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User created successfully.")
                    } else {
                        Log.w(TAG, "Error adding document", task.exception)
                        Log.e(TAG, "Error creating user: ${task.exception?.message}")
                    }
                }
        } else {
            Log.e(TAG, "Current user is null")
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                birthDateET.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
