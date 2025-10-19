package de.florianostertag.coffeehelper.api

import de.florianostertag.coffeehelper.data.Bean
import de.florianostertag.coffeehelper.data.Extraction
import de.florianostertag.coffeehelper.data.LoginRequest
import de.florianostertag.coffeehelper.data.LoginResponse
import retrofit2.http.*

interface CoffeeApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/bean")
    suspend fun getAllBeans(): List<Bean>

    @GET("api/extraction/for-bean/{beanId}")
    suspend fun getExtractionsForBean(@Path("beanId") beanId: Long): List<Extraction>
}