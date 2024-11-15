package com.mab.buwisbuddyph.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mab.buwisbuddyph.R

class DocumentDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val documentImageView: ImageView = view.findViewById(R.id.documentImageView)
        val documentImgLink = arguments?.getString("documentImgLink") ?: ""
        val returnIcon: ImageView = view.findViewById(R.id.returnIcon)

        returnIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        Glide.with(this)
            .load(documentImgLink)
            .into(documentImageView)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(documentImgLink: String): DocumentDetailFragment {
            val fragment = DocumentDetailFragment()
            val args = Bundle().apply {
                putString("documentImgLink", documentImgLink)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
