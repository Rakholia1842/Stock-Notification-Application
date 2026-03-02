package com.stockalert.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockalert.data.api.StockPriceResponse
import com.stockalert.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchStocks(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val result = repository.searchStocks(query)
            _uiState.value = if (result.isSuccess) {
                val stocks = result.getOrNull() ?: emptyList()
                if (stocks.isEmpty()) {
                    HomeUiState.NoResults
                } else {
                    HomeUiState.Success(stocks)
                }
            } else {
                HomeUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = HomeUiState.Idle
    }
}

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    object NoResults : HomeUiState()
    data class Success(val stocks: List<StockPriceResponse>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
