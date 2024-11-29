package com.acosta.eldriod.calendar.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acosta.eldriod.R
import com.acosta.eldriod.calendar.adapters.CalendarAdapter
import com.acosta.eldriod.calendar.adapters.EventAdapter
import com.acosta.eldriod.calendar.utils.CalendarUtils
import com.acosta.eldriod.viewmodel.EventViewModel
import java.time.LocalDate

class WeekViewFragment : Fragment(), CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var eventListView: ListView
    private lateinit var eventViewModel: EventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_week_view, container, false)
        initWidgets(view)

        eventViewModel = ViewModelProvider(this).get(EventViewModel::class.java)

        setWeekView()

        view.findViewById<Button>(R.id.newEventAction).setOnClickListener { newEventAction() }
        view.findViewById<Button>(R.id.previousWeekAction).setOnClickListener { previousWeekAction() }
        view.findViewById<Button>(R.id.nextWeekAction).setOnClickListener { nextWeekAction() }

        return view
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthYearText = view.findViewById(R.id.monthYearTV)
        eventListView = view.findViewById(R.id.eventListView)
    }

    private fun setWeekView() {
        CalendarUtils.selectedDate?.let {
            monthYearText.text = CalendarUtils.monthYearFromDate(it)

            val days = CalendarUtils.daysInWeekArray(it)
            val calendarAdapter = CalendarAdapter(ArrayList(days), this)

            calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
            calendarRecyclerView.adapter = calendarAdapter
        }

        setEventAdapter()
    }

    private fun setEventAdapter() {
        CalendarUtils.selectedDate?.let { selectedDate ->
            eventViewModel.events.observe(viewLifecycleOwner) { events ->
                val dailyEvents = events.filter { it.date == selectedDate.toString() }
                val eventAdapter = EventAdapter(requireContext(), dailyEvents)
                eventListView.adapter = eventAdapter
            }
        } ?: Toast.makeText(requireContext(), "No date selected", Toast.LENGTH_SHORT).show()
    }

    override fun onItemClick(position: Int, date: LocalDate) {
        CalendarUtils.selectedDate = date
        setWeekView()
    }

    private fun previousWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.minusWeeks(1)
        setWeekView()
    }

    private fun nextWeekAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.plusWeeks(1)
        setWeekView()
    }

    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }

    private fun newEventAction() {
        Log.d("WeekViewFragment", "Navigating to EventFragment")
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, EventFragment())
            .addToBackStack(null)
            .commit()
    }
}
