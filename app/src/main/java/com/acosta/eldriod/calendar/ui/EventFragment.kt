package com.acosta.eldriod.calendar.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.acosta.eldriod.R
import com.acosta.eldriod.calendar.utils.CalendarUtils
import com.acosta.eldriod.models.Event
import com.acosta.eldriod.viewmodel.EventViewModel
import java.time.LocalDate
import java.time.LocalTime

class EventFragment : Fragment() {

    private lateinit var eventNameET: EditText
    private lateinit var eventDateTV: TextView
    private lateinit var eventTimeTV: TextView
    private lateinit var saveEventAction: Button
    private lateinit var eventViewModel: EventViewModel

    private lateinit var time: LocalTime
    private lateinit var selectedDate: LocalDate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        initWidgets(view)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        selectedDate = CalendarUtils.selectedDate ?: LocalDate.now()
        time = LocalTime.now()
        eventDateTV.text = "Date: ${CalendarUtils.formattedDate(selectedDate)}"
        eventTimeTV.text = "Time: ${CalendarUtils.formattedTime(time.toString())}"
        saveEventAction.setOnClickListener {
            saveEvent()
        }

        return view
    }

    private fun initWidgets(view: View) {
        eventNameET = view.findViewById(R.id.eventNameET)
        eventDateTV = view.findViewById(R.id.eventDateTV)
        eventTimeTV = view.findViewById(R.id.eventTimeTV)
        saveEventAction = view.findViewById(R.id.saveEventAction)
    }

    private fun saveEvent() {
        val eventName = eventNameET.text.toString().trim()

        if (eventName.isEmpty()) {
            Toast.makeText(requireContext(), "Event name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val newEvent = Event(
            id = 0,
            name = eventName,
            date = selectedDate.toString(),
            time = time.toString()
        )

        eventViewModel.addEvent(newEvent).observe(viewLifecycleOwner) { event ->
            if (event != null) {
                Toast.makeText(requireContext(), "Event saved successfully", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager?.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to save event", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
