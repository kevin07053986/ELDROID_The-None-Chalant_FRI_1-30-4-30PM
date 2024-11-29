package com.acosta.eldriod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.acosta.eldriod.models.Event
import com.acosta.eldriod.repository.EventRepository
import kotlinx.coroutines.Dispatchers

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    val events = liveData(Dispatchers.IO) {
        emit(repository.fetchEvents())
    }

    fun addEvent(event: Event) = liveData(Dispatchers.IO) {
        emit(repository.addEvent(event))
    }

    fun updateEvent(id: Int, event: Event) = liveData(Dispatchers.IO) {
        emit(repository.updateEvent(id, event))
    }

    fun deleteEvent(id: Int) = liveData(Dispatchers.IO) {
        emit(repository.deleteEvent(id))
    }
}
