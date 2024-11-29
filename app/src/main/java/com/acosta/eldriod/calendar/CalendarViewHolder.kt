package com.acosta.eldriod.calendar

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acosta.eldriod.R
import com.acosta.eldriod.calendar.adapters.CalendarAdapter
import java.time.LocalDate

class CalendarViewHolder(
    itemView: View,
    private val onItemListener: CalendarAdapter.OnItemListener,
    private val days: ArrayList<LocalDate?>
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val parentView: View = itemView.findViewById(R.id.parentView)
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        days[adapterPosition]?.let { onItemListener.onItemClick(adapterPosition, it) }
    }
}

