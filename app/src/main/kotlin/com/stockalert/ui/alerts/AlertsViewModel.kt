package com.stockalert.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockalert.data.db.PriceAlertEntity
import com.stockalert.data.repository.StockRepository
import com.stockalert.domain.model.AlertStatus
import com.stockalert.domain.model.AlertType
import com.stockalert.domain.model.PriceAlert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    val alerts: StateFlow<List<PriceAlert>> = repository.getAllAlerts().map { entities ->
        entities.map { entity ->
            PriceAlert(
                id = entity.id,
                stockSymbol = entity.stockSymbol,
                stockName = entity.stockName,
                targetPrice = entity.targetPrice,
                alertType = AlertType.valueOf(entity.alertType),
                status = AlertStatus.valueOf(entity.status),
                createdAt = entity.createdAt,
                triggeredAt = entity.triggeredAt
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    fun deleteAlert(alertId: Int) {
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading
            val result = repository.deleteAlert(alertId)
            _deleteState.value = if (result.isSuccess) {
                DeleteState.Success
            } else {
                DeleteState.Error("Failed to delete alert")
            }
        }
    }

    fun resetAlert(alertId: Int) {
        viewModelScope.launch {
            repository.resetAlert(alertId)
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }
}

sealed class DeleteState {
    object Idle : DeleteState()
    object Loading : DeleteState()
    object Success : DeleteState()
    data class Error(val message: String) : DeleteState()
}
