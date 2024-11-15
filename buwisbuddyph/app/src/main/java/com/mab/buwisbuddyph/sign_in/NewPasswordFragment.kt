package com.mab.buwisbuddyph.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mab.buwisbuddyph.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class NewPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to EditText and Button from layout
        val newPasswordEditText: TextInputEditText = view.findViewById(R.id.newPasswordEditText)
        val confirmPasswordEditText: TextInputEditText = view.findViewById(R.id.confirmPasswordEditText)
        val submitButton: MaterialButton = view.findViewById(R.id.submitNewPasswordButton)

        // Set up onClickListener to call onNewPasswordSubmit function
        submitButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Check if passwords match, then call onNewPasswordSubmit
            if (newPassword == confirmPassword) {
                onNewPasswordSubmit()
            } else {
                // Handle password mismatch if needed (e.g., show a message to the user)
            }
        }
    }

    // Define the onNewPasswordSubmit function to perform navigation
    private fun onNewPasswordSubmit() {
        // Navigate to HomeActivity
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
    }
}
