package com.mab.buwisbuddyph.sign_in

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mab.buwisbuddyph.LoadingDialog
import com.mab.buwisbuddyph.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class SignUpFragment : Fragment() {

    private lateinit var userProfileImg: CircleImageView
    private lateinit var userFullNameET: EditText
    private lateinit var birthDateET: EditText
    private lateinit var userEmailET: EditText
    private lateinit var userPasswordET: EditText
    private lateinit var userPasswordConfirmationET: EditText
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "SignUpFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        loadingDialog = LoadingDialog(requireContext())

        userProfileImg = view.findViewById(R.id.userProfileImg)
        userFullNameET = view.findViewById(R.id.userFullNameET)
        birthDateET = view.findViewById(R.id.birthDateET)
        userEmailET = view.findViewById(R.id.userEmailET)
        userPasswordET = view.findViewById(R.id.userPasswordET)
        userPasswordConfirmationET = view.findViewById(R.id.userPasswordConfirmationET)

        val spinner: Spinner = view.findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            requireContext(),
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
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            pickImageContract.launch(intent)
        }
        val createAccountButton: Button = view.findViewById(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            createUser(spinner.selectedItem.toString())
        }
    }

    private val pickImageContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageUri = result.data?.data
            userProfileImg.setImageURI(imageUri)
            userProfileImg.tag = imageUri.toString()
        }
    }

    private fun setEditTextBackground(editText: EditText, isEmpty: Boolean) {
        editText.setBackgroundResource(if (isEmpty) R.drawable.empty_fields_border else R.drawable.square_border_default)
    }

    private fun createUser(userAccountType: String) {
        val userFullName = userFullNameET.text.toString().trim()
        val birthDate = birthDateET.text.toString().trim()
        val userEmail = userEmailET.text.toString().trim()
        val userPassword = userPasswordET.text.toString().trim()
        val userPasswordConfirmation = userPasswordConfirmationET.text.toString().trim()

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
            .addOnCompleteListener(requireActivity()) { task ->
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
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show()
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
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                birthDateET.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }
}
