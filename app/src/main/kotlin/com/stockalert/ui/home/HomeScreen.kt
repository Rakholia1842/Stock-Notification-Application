package com.stockalert.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockalert.ui.components.ErrorMessage
import com.stockalert.ui.components.LoadingIndicator
import com.stockalert.ui.components.NoResultsMessage
import com.stockalert.ui.components.StockSearchResultItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStockSelected: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("StockAlert") }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    placeholder = { Text("Search stocks...") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    }
                )

                Button(
                    onClick = { viewModel.searchStocks(searchQuery) },
                    modifier = Modifier.height(50.dp),
                    enabled = searchQuery.isNotEmpty()
                ) {
                    Text("Search")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on state
            when (val state = uiState) {
                is HomeUiState.Idle -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Search for Indian stocks",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enter a company name or NSE ticker symbol",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is HomeUiState.Loading -> {
                    LoadingIndicator(modifier = Modifier.padding(top = 32.dp))
                }

                is HomeUiState.NoResults -> {
                    NoResultsMessage(modifier = Modifier.padding(top = 32.dp))
                }

                is HomeUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp)
                    ) {
                        items(state.stocks) { stock ->
                            StockSearchResultItem(
                                stock = stock,
                                onClick = onStockSelected,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                is HomeUiState.Error -> {
                    ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.searchStocks(searchQuery) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                }
            }
        }
    }
}
