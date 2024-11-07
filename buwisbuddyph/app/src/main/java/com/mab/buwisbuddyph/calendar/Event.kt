package com.mab.buwisbuddyph.calendar

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    var name: String,
    var date: LocalDate,
    var time: LocalTime
) {
    companion object {
        val eventsList = ArrayList<Event>()

        fun eventsForDate(date: LocalDate): List<Event> {
            return eventsList.filter { it.date == date }
        }
    }
}

