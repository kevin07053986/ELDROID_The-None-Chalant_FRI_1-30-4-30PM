package com.mab.buwisbuddyph.accountant

import android.widget.Filter
import android.widget.Filterable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.User
import de.hdodenhof.circleimageview.CircleImageView

class AccountantAdapter(
    private var userList: List<User>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AccountantAdapter.AccountantViewHolder>(), Filterable {

    private var userListFiltered: List<User> = userList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.accountants_list_item, parent, false)
        return AccountantViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountantViewHolder, position: Int) {
        val user = userListFiltered[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClick(user.userID)
        }
    }

    override fun getItemCount() = userListFiltered.size

    inner class AccountantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val accountantProfileImage: CircleImageView = itemView.findViewById(R.id.accountantProfileImage)
        private val accountantName: TextView = itemView.findViewById(R.id.accountantName)

        fun bind(user: User) {
            accountantName.text = user.userFullName
            Glide.with(itemView.context)
                .load(user.userProfileImage)
                .placeholder(R.drawable.default_profile_img)
                .error(R.drawable.default_profile_img)
                .into(accountantProfileImage)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                userListFiltered = if (charString.isEmpty()) userList else {
                    userList.filter {
                        it.userFullName.contains(charString, ignoreCase = true)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = userListFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userListFiltered = if (results?.values == null)
                    emptyList()
                else
                    results.values as List<User>
                notifyDataSetChanged()
            }
        }
    }
}
