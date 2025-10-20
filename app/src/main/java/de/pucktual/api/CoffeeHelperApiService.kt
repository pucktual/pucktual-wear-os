package de.pucktual.api

import de.pucktual.data.Bean
import de.pucktual.data.Extraction
import de.pucktual.data.LoginRequest
import de.pucktual.data.LoginResponse
import retrofit2.http.*

interface CoffeeApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/bean")
    suspend fun getAllBeans(): List<Bean>

    @GET("api/extraction/for-bean/{beanId}")
    suspend fun getExtractionsForBean(@Path("beanId") beanId: Long): List<Extraction>
}