package com.stockalert.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockalert.domain.model.AlertType
import com.stockalert.ui.components.ErrorMessage
import com.stockalert.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val showAlertDialog by viewModel.showAlertDialog.collectAsState()
    val targetPrice by viewModel.targetPrice.collectAsState()
    val alertType by viewModel.alertType.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadStockDetails(symbol)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stock?.name ?: "Stock Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                is DetailUiState.Loading -> {
                    LoadingIndicator()
                }

                is DetailUiState.Success -> {
                    stock?.let {
                        DetailContent(
                            stock = it,
                            onSetAlert = { viewModel.showAlertDialog() }
                        )
                    }
                }

                is DetailUiState.AlertSaved -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✓ Alert Saved!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Your price alert has been created successfully.")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            viewModel.resetStatus()
                            onBack()
                        }) {
                            Text("Go Back")
                        }
                    }
                }

                is DetailUiState.Error -> {
                    ErrorMessage(
                        message = (uiState as DetailUiState.Error).message,
                        onRetry = { viewModel.loadStockDetails(symbol) }
                    )
                }

                is DetailUiState.Idle -> {}
            }
        }
    }

    // Alert Dialog
    if (showAlertDialog) {
        PriceAlertDialog(
            onDismiss = { viewModel.dismissAlertDialog() },
            onSave = { viewModel.saveAlert() },
            targetPrice = targetPrice,
            onTargetPriceChange = { viewModel.updateTargetPrice(it) },
            alertType = alertType,
            onAlertTypeChange = { viewModel.updateAlertType(it) }
        )
    }
}

@Composable
fun DetailContent(
    stock: com.stockalert.data.api.StockPriceResponse,
    onSetAlert: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with price
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = stock.symbol,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${String.format("%.2f", stock.currentPrice)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (stock.percentChange >= 0) "+" else "",
                        fontSize = 14.sp,
                        color = if (stock.percentChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Text(
                        text = "${String.format("%.2f", stock.percentChange)}%",
                        fontSize = 14.sp,
                        color = if (stock.percentChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Details grid
        StockDetailRow("Day High", "₹${String.format("%.2f", stock.dayHigh)}")
        Spacer(modifier = Modifier.height(12.dp))

        StockDetailRow("Day Low", "₹${String.format("%.2f", stock.dayLow)}")
        Spacer(modifier = Modifier.height(12.dp))

        StockDetailRow("Volume", formatVolume(stock.volume))
        Spacer(modifier = Modifier.height(12.dp))

        StockDetailRow("Previous Close", "₹${String.format("%.2f", stock.previousClose)}")

        Spacer(modifier = Modifier.height(32.dp))

        // Set Alert Button
        Button(
            onClick = onSetAlert,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Set Price Alert", fontSize = 16.sp)
        }
    }
}

@Composable
fun StockDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PriceAlertDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    targetPrice: String,
    onTargetPriceChange: (String) -> Unit,
    alertType: AlertType,
    onAlertTypeChange: (AlertType) -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Set Price Alert",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Target Price Input
                OutlinedTextField(
                    value = targetPrice,
                    onValueChange = onTargetPriceChange,
                    label = { Text("Target Price (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Alert Type Selection
                Text(
                    text = "Alert Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                AlertType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = alertType == type,
                            onClick = { onAlertTypeChange(type) }
                        )
                        Text(
                            text = type.displayName,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onSave,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

private fun formatVolume(volume: Long): String {
    return when {
        volume >= 1_000_000_000 -> "${String.format("%.2f", volume / 1_000_000_000.0)} B"
        volume >= 1_000_000 -> "${String.format("%.2f", volume / 1_000_000.0)} M"
        volume >= 1_000 -> "${String.format("%.2f", volume / 1_000.0)} K"
        else -> volume.toString()
    }
}
