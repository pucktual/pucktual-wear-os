package de.pucktual.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.pucktual.api.CoffeeApiService
import de.pucktual.data.Extraction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class ExtractionDetailViewModel(
    private val beanId: Long,
    private val apiService: CoffeeApiService
) : ViewModel() {

    protected val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Loading : UiState()
        data class Success(val extractions: List<Extraction>) : UiState()
        object Empty : UiState() // Falls keine Extraktionen gefunden werden
        data class Error(val message: String) : UiState()
    }

    init {
        loadExtractions()
    }

    protected open fun loadExtractions() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                // API-Aufruf zur Abrufung aller Extraktionen für die gegebene Bohne
                val extractions = apiService.getExtractionsForBean(beanId)

                if (extractions.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    // Sortiere die Extraktionen nach ID absteigend, um die neueste zuerst zu haben
                    val sortedExtractions = extractions.sortedByDescending { it.id }
                    _uiState.value = UiState.Success(sortedExtractions)
                }
            } catch (e: Exception) {
                // Fehler beim Laden (z.B. Netzwerk, ungültiger Token)
                _uiState.value = UiState.Error("Fehler beim Laden der Rezepte: ${e.message}")
            }
        }
    }

    class Factory(
        private val beanId: Long,
        private val apiService: CoffeeApiService
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExtractionDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExtractionDetailViewModel(beanId, apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
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
}