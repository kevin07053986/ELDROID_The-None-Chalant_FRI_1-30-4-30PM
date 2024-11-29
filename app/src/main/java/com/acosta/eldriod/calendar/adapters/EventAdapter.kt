package com.acosta.eldriod.calendar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.acosta.eldriod.R
import com.acosta.eldriod.models.Event
import com.acosta.eldriod.calendar.utils.CalendarUtils

class EventAdapter(context: Context, private val events: List<Event>) : ArrayAdapter<Event>(context, 0, events) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.event_cell, parent, false)
        val event = getItem(position)

        event?.let {
            val eventCellTV = view.findViewById<TextView>(R.id.eventCellTV)
            val eventTitle = "${it.name} ${CalendarUtils.formattedTime(it.time)}"
            eventCellTV.text = eventTitle
        }

        return view
    }
}
