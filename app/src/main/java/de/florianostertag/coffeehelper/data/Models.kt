package de.florianostertag.coffeehelper.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Bean(
    val id: Long,
    val name: String,
    val manufacturer: String
)

@JsonClass(generateAdapter = true)
data class Extraction(
    val id: Long,
    @Json(name = "in") val inputGrams: Double,
    @Json(name = "out") val outputGrams: Double,
    val time: Int,
    val grind: Int?,
    val nextExtractionHint: String?
)