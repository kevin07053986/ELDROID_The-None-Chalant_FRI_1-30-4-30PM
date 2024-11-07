package com.mab.buwisbuddyph.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.calendar.CalendarUtils.daysInMonthArray
import com.mab.buwisbuddyph.calendar.CalendarUtils.monthYearFromDate
import java.time.LocalDate

class CalendarFragment : Fragment(), CalendarAdapter.OnItemListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        initWidgets(view)
        CalendarUtils.selectedDate = LocalDate.now()

        val weeklyButton: Button = view.findViewById(R.id.weeklyButton)
        weeklyButton.setOnClickListener{
            weeklyAction(view)
        }

        val previousMonthAction: Button = view.findViewById(R.id.previousMonthButton)
        previousMonthAction.setOnClickListener{
            previousMonthAction(view)
        }

        val nextMonthAction: Button = view.findViewById(R.id.nextMonthButton)
        nextMonthAction.setOnClickListener{
            nextMonthAction(view)
        }

        setMonthView()
        return view
    }

    private fun initWidgets(view: View) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthYearText = view.findViewById(R.id.monthYearTV)
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(CalendarUtils.selectedDate!!)
        val daysInMonthStrings = daysInMonthArray(CalendarUtils.selectedDate!!)

        val calendarAdapter = CalendarAdapter(daysInMonthStrings, this)
        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
    }

    fun previousMonthAction(view: View) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate!!.plusMonths(1)
        setMonthView()
    }

    override fun onItemClick(position: Int, date: LocalDate) {
        CalendarUtils.selectedDate = date
        setMonthView()
    }

    fun weeklyAction(view: View) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, WeekViewFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
