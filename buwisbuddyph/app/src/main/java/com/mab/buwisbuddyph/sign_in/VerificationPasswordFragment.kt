package com.mab.buwisbuddyph.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mab.buwisbuddyph.R
import com.google.android.material.button.MaterialButton

class VerificationPasswordFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verification_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up button listeners
        view.findViewById<MaterialButton>(R.id.verifyCodeButton).setOnClickListener {
            onVerifyCode()
        }

        view.findViewById<MaterialButton>(R.id.signUpTextView).setOnClickListener {
            onSignUpClick()
        }
    }

    private fun onVerifyCode() {
        // Navigate to NewPasswordFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NewPasswordFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun onSignUpClick() {
        // Navigate to SignUpFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SignUpFragment())
            .addToBackStack(null)
            .commit()
    }
}
