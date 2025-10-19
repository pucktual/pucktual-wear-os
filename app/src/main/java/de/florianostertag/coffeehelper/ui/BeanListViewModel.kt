package de.florianostertag.coffeehelper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import de.florianostertag.coffeehelper.api.ApiClient
import de.florianostertag.coffeehelper.api.CoffeeApiService
import de.florianostertag.coffeehelper.data.Bean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class BeanListViewModel(
    private val apiService: CoffeeApiService
) : ViewModel() {

    val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    sealed class UiState {
        object Loading : UiState()
        data class Success(val beans: List<Bean>) : UiState()
        data class Error(val message: String) : UiState()
    }

    init {
        loadBeans()
    }

    open fun loadBeans() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val beans = apiService.getAllBeans()
                _uiState.value = UiState.Success(beans)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Fehler beim Laden: ${e.message}")
            }
        }
    }

    class Factory(private val apiService: CoffeeApiService) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BeanListViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BeanListViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}