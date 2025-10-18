package de.florianostertag.coffeehelper.api

import de.florianostertag.coffeehelper.data.Bean
import de.florianostertag.coffeehelper.data.Extraction
import retrofit2.http.*

interface CoffeeApiService {

    @GET("api/bean")
    suspend fun getAllBeans(): List<Bean>

    @GET("api/extraction/for-bean/{beanId}")
    suspend fun getExtractionsForBean(@Path("beanId") beanId: Long): List<Extraction>
}