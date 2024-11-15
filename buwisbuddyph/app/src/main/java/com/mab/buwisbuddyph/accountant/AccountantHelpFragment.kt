package com.mab.buwisbuddyph.accountant

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.User

class AccountantHelpFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var accountantAdapter: AccountantAdapter
    private lateinit var searchView: SearchView
    private val userList = mutableListOf<User>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_accountant_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        accountantAdapter = AccountantAdapter(userList) { userID ->
            val fragment = AccountantProfileFragment()
            fragment.arguments = Bundle().apply {
                putString("userID", userID)
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = accountantAdapter

        searchView = view.findViewById(R.id.search_accountants)
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                accountantAdapter.filter.filter(newText)
                return false
            }
        })

        fetchAccountants()

        val returnIcon = view.findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun fetchAccountants() {
        db.collection("users")
            .whereEqualTo("userAccountType", "Accountant")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val user = document.toObject<User>()
                        userList.add(user)
                    }
                    accountantAdapter.notifyDataSetChanged()
                } else {
                    Log.d("AccountantHelpFragment", "Error getting documents: ", task.exception)
                }
            }
    }
}
