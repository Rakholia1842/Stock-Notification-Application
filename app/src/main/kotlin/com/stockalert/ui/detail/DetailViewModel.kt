package com.stockalert.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockalert.data.api.StockPriceResponse
import com.stockalert.data.repository.StockRepository
import com.stockalert.domain.model.AlertType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _stock = MutableStateFlow<StockPriceResponse?>(null)
    val stock: StateFlow<StockPriceResponse?> = _stock.asStateFlow()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Idle)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _showAlertDialog = MutableStateFlow(false)
    val showAlertDialog: StateFlow<Boolean> = _showAlertDialog.asStateFlow()

    private val _targetPrice = MutableStateFlow("")
    val targetPrice: StateFlow<String> = _targetPrice.asStateFlow()

    private val _alertType = MutableStateFlow(AlertType.ABOVE)
    val alertType: StateFlow<AlertType> = _alertType.asStateFlow()

    fun loadStockDetails(symbol: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            val result = repository.getStockPrice(symbol)
            _uiState.value = if (result.isSuccess) {
                _stock.value = result.getOrNull()
                DetailUiState.Success
            } else {
                DetailUiState.Error(result.exceptionOrNull()?.message ?: "Can't load stock data")
            }
        }
    }

    fun showAlertDialog() {
        _showAlertDialog.value = true
        _targetPrice.value = ""
        _alertType.value = AlertType.ABOVE
    }

    fun dismissAlertDialog() {
        _showAlertDialog.value = false
    }

    fun updateTargetPrice(price: String) {
        _targetPrice.value = price
    }

    fun updateAlertType(type: AlertType) {
        _alertType.value = type
    }

    fun saveAlert() {
        val stock = _stock.value ?: return
        val price = _targetPrice.value.toDoubleOrNull() ?: return

        if (price <= 0) {
            _uiState.value = DetailUiState.Error("Price must be greater than 0")
            return
        }

        viewModelScope.launch {
            val result = repository.createAlert(
                stockSymbol = stock.symbol,
                stockName = stock.name,
                targetPrice = price,
                alertType = _alertType.value.name
            )

            if (result.isSuccess) {
                _showAlertDialog.value = false
                _uiState.value = DetailUiState.AlertSaved
            } else {
                _uiState.value = DetailUiState.Error("Failed to save alert")
            }
        }
    }

    fun resetStatus() {
        _uiState.value = DetailUiState.Success
    }
}

sealed class DetailUiState {
    object Idle : DetailUiState()
    object Loading : DetailUiState()
    object Success : DetailUiState()
    object AlertSaved : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
