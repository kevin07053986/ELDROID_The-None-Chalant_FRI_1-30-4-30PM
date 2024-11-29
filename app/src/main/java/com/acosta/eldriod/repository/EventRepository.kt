package com.acosta.eldriod.repository

import com.acosta.eldriod.models.Event
import com.acosta.eldriod.network.ApiService
import com.acosta.eldriod.network.RetrofitInstance

class EventRepository {
    private val apiService = RetrofitInstance.createService(ApiService::class.java)

    suspend fun fetchEvents(): List<Event> = apiService.getAllEvents()

    suspend fun addEvent(event: Event): Event = apiService.createEvent(event)

    suspend fun updateEvent(id: Int, event: Event): Event = apiService.updateEvent(id, event)

    suspend fun deleteEvent(id: Int): Boolean {
        val response = apiService.deleteEvent(id)
        return response.isSuccessful
    }
}
