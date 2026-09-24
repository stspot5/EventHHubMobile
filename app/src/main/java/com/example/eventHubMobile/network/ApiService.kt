package com.example.eventHubMobile.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("api/events")
    suspend fun getAllEvents(): Response<List<EventDto>>

    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<EventDto>

    @POST("api/events")
    suspend fun createEvent(@Body event: CreateEventRequest): Response<EventDto>

    @POST("api/users/auth/register")
    suspend fun register(@Body user: UserDto): Response<ResponseBody>

    @POST("api/users/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<Map<String, Any>>

    @GET("api/users/users")
    suspend fun getAllUsers(): Response<List<UserDto>>

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body user: UserDto
    ): Response<ResponseBody>

    @DELETE("api/users/email/{email}")
    suspend fun deleteUserByEmail(
        @Path("email") email: String
    ): Response<ResponseBody>

    @POST("api/tickets/book")
    suspend fun bookEvent(
        @Query("userId") userId: Long,
        @Query("eventId") eventId: Long
    ): Response<ResponseBody>

    @GET("api/tickets/user/{userId}")
    suspend fun getMyEvents(@Path("userId") userId: Long): Response<List<TicketDto>>

    @GET("api/notifications/user/{userId}")
    suspend fun getNotifications(@Path("userId") userId: Long): Response<List<NotificationDto>>

    @DELETE("api/notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Long): Response<Void>

    @GET("api/messages/conversations/{userId}")
    suspend fun getConversations(@Path("userId") userId: Long): Response<List<ChatSummaryDto>>

    @GET("api/messages/history")
    suspend fun getChatHistory(
        @Query("user1") user1Id: Long,
        @Query("user2") user2Id: Long
    ): Response<List<MessageDto>>

    @POST("api/messages/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<MessageDto>
}
