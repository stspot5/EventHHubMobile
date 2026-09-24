package com.example.eventHubMobile

import com.example.eventHubMobile.network.EventDto
import com.example.eventHubMobile.ui.viewmodels.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class EventMappingTest {

    @Test
    fun `test event dto to domain mapping`() {

        val dto = EventDto(
            id = 10L,
            name = "Test Event",
            type = "Music",
            status = "Active",
            description = "Test Description",
            ticketPrice = 25.0,
            currency = "EUR",
            availableTickets = 100,
            totalTickets = 100,
            ticketsSold = 0,
            startDateTime = "2024-10-15T10:00:00",
            endDateTime = "2024-10-15T12:00:00",
            coverImageUrl = null,
            roomName = "Hall 1",
            location = null
        )


        val domain = dto.toDomain()


        assertEquals(10L, domain.id)
        assertEquals("Test Event", domain.title)
        assertEquals(25.0, domain.price, 0.0)
        assertEquals("Music", domain.category)
    }
}
