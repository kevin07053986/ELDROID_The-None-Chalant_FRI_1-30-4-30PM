package com.mab.buwisbuddyph.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.calendar.CalendarUtils.daysInWeekArray
import com.mab.buwisbuddyph.calendar.CalendarUtils.monthYearFromDate
import java.time.LocalDate

class WeekViewFragment : Fragment(), CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var eventListView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_week_view, container, false)
        initWidgets(view)
        setWeekView()

        val newEventAction: Button = view.findViewById(R.id.newEventAction)
        newEventAction.setOnClickListener{
            newEventAction(view)
        }

        val previousWeekAction: Button = view.findViewById(R.id.previousWeekAction)
        previousWeekAction.setOnClickListener{
            previousWeekAction(view)
        }

        val nextWeekAction: Button = view.findViewById(R.id.nextWeekAction)
        nextWeekAction.setOnClickListener{
            nextWeekAction(view)
        }

        return view
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthYearText = view.findViewById(R.id.monthYearTV)
        eventListView = view.findViewById(R.id.eventListView)
    }

    private fun setWeekView() {
        monthYearText.text = CalendarUtils.selectedDate?.let { monthYearFromDate(it) }
        val days = CalendarUtils.selectedDate?.let { daysInWeekArray(it) }
        val daysArrayList = ArrayList<LocalDate?>(days ?: emptyList()) // Convert to ArrayList<LocalDate?>
        val calendarAdapter = CalendarAdapter(daysArrayList, this)
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
        setEventAdapter()
    }


    override fun onItemClick(position: Int, date: LocalDate) {
        CalendarUtils.selectedDate = date
        setWeekView()
    }

    private fun setEventAdapter() {
        val dailyEvents = CalendarUtils.selectedDate?.let { Event.eventsForDate(it) }
        val eventAdapter = dailyEvents?.let { EventAdapter(requireContext(), it) }
        eventListView.adapter = eventAdapter
    }

    fun previousWeekAction(view: View) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.minusWeeks(1)
        setWeekView()
    }

    fun nextWeekAction(view: View) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate?.plusWeeks(1)
        setWeekView()
    }

    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }

    fun newEventAction(view: View) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, EventFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}
