package de.florianostertag.coffeehelper.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.florianostertag.coffeehelper.api.RetrofitClient
import de.florianostertag.coffeehelper.data.Extraction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ExtractionDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val beanId: Long = checkNotNull(savedStateHandle["beanId"])

    val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Loading : UiState()
        data class Success(val extractions: List<Extraction>) : UiState()
        object Empty : UiState()
        data class Error(val message: String) : UiState()
    }

    init {
        loadExtractions()
    }

    private fun loadExtractions() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val extractions = RetrofitClient.apiService.getExtractionsForBean(beanId)

                if (extractions.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    val sortedExtractions = extractions.sortedByDescending { it.id }
                    _uiState.value = UiState.Success(sortedExtractions)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Fehler beim Laden der Rezepte: ${e.message}")
            }
        }
    }
}

fun getMockExtractions(): List<Extraction> {
    return listOf(
        // Neuestes/Bestes Rezept (wird als Card angezeigt)
        Extraction(
            id = 105,
            inputGrams = 18.2,
            outputGrams = 38.5,
            time = 28,
            grind = 4,
            nextExtractionHint = "Mahle etwas feiner, der Durchlauf war zu schnell."
        ),
        // Älteres Rezept (wird in der History-Liste angezeigt)
        Extraction(
            id = 104,
            inputGrams = 18.0,
            outputGrams = 36.0,
            time = 35,
            grind = 5,
            nextExtractionHint = null
        ),
        // Noch älteres Rezept
        Extraction(
            id = 103,
            inputGrams = 18.5,
            outputGrams = 40.0,
            time = 25,
            grind = 3,
            nextExtractionHint = null
        )
    )
}