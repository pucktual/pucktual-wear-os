package de.pucktual.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "access_token") val accessToken: String
)

@JsonClass(generateAdapter = true)
data class Bean(
    val id: Long,
    val name: String,
    val manufacturer: String,
    @Json(name = "decaf")
    val decafNullable: Boolean? = false
) {
    val decaf: Boolean = decafNullable ?: false
}

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
        Bean(id = 1, name = "Kolumbien La Palma", manufacturer = "Röster XYZ", decafNullable = false),
        Bean(id = 2, name = "Äthiopien Yirgacheffe", manufacturer = "Spezialitätenrösterei", decafNullable = false),
        Bean(id = 3, name = "Brasilien Santos", manufacturer = "Großröster", decafNullable = true),
        Bean(id = 4, name = "Brasilien Santos", manufacturer = "Großröster", decafNullable = false),
        Bean(id = 5, name = "Brasilien Santos", manufacturer = "Großröster", decafNullable = true),
        Bean(id = 6, name = "Brasilien Santos", manufacturer = "Großröster", decafNullable = false)
    )
}

fun getMockExtractions(): List<Extraction> {
    return listOf(
        Extraction(id = 1, inputGrams = 18.0, outputGrams = 42.0, time = 25, grind = 17, nextExtractionHint = "Next hint"),
        Extraction(id = 2, inputGrams = 18.4, outputGrams = 42.0, time = 30, grind = 20, nextExtractionHint = "Next hint"),
        Extraction(id = 3, inputGrams = 18.7, outputGrams = 42.0, time = 28, grind = 22, nextExtractionHint = "Next hint"),
    )
}