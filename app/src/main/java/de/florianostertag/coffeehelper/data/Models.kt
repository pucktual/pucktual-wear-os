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

fun getMockBeans(): List<Bean> {
    return listOf(
        Bean(id = 1, name = "Kolumbien La Palma", manufacturer = "Röster XYZ"),
        Bean(id = 2, name = "Äthiopien Yirgacheffe", manufacturer = "Spezialitätenrösterei"),
        Bean(id = 3, name = "Brasilien Santos", manufacturer = "Großröster")
    )
}