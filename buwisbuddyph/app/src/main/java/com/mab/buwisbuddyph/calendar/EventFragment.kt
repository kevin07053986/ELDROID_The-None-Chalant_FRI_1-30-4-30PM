package com.mab.buwisbuddyph.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mab.buwisbuddyph.R
import java.time.LocalTime

class EventFragment : Fragment() {

    private lateinit var eventNameET: EditText
    private lateinit var eventDateTV: TextView
    private lateinit var eventTimeTV: TextView

    private lateinit var time: LocalTime

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        initWidgets(view)
        time = LocalTime.now()
        eventDateTV.text = "Date: ${CalendarUtils.formattedDate(CalendarUtils.selectedDate!!)}"
        eventTimeTV.text = "Time: ${CalendarUtils.formattedTime(time)}"

        val saveEventAction: Button = view.findViewById(R.id.saveEventAction)
        saveEventAction.setOnClickListener{
            saveEventAction(view)
        }

        return view
    }

    private fun initWidgets(view: View) {
        eventNameET = view.findViewById(R.id.eventNameET)
        eventDateTV = view.findViewById(R.id.eventDateTV)
        eventTimeTV = view.findViewById(R.id.eventTimeTV)
    }

    fun saveEventAction(view: View) {
        val eventName = eventNameET.text.toString()
        val newEvent = Event(eventName, CalendarUtils.selectedDate!!, time)
        Event.eventsList.add(newEvent)
        activity?.supportFragmentManager?.popBackStack()
    }
}
