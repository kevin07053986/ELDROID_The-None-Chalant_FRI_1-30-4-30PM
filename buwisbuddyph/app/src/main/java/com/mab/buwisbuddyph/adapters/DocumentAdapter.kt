package com.mab.buwisbuddyph.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.dataclass.Document
import com.mab.buwisbuddyph.home.DocumentDetailActivity

class DocumentAdapter(private val context: Context) : RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder>() {

    private var documents: List<Document> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val document = documents[position]
        holder.bind(document)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DocumentDetailActivity::class.java).apply {
                putExtra("documentId", document.documentId)
                putExtra("documentImgLink", document.documentImgLink)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return documents.size
    }

    fun setDocuments(documents: List<Document>) {
        this.documents = documents
        notifyDataSetChanged()
    }

    class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val documentImageView: ImageView = itemView.findViewById(R.id.documentImageView)

        fun bind(document: Document) {
            Glide.with(itemView.context)
                .load(document.documentImgLink)
                .into(documentImageView)
        }
    }
}
