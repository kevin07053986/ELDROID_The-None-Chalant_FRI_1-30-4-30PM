package com.mab.buwisbuddyph.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.home.HomeFragment


class GuideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(
            R.layout.fragment_guide,
            container,
            false
        ) // Update layout file name as needed
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val returnIcon = view.findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // Navigate back to HomeFragment or replace with another fragment as needed
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.frameLayout,
                    HomeFragment()
                ) // Update container ID and fragment as needed
                .addToBackStack(null)
                .commit()
        }
    }
}

