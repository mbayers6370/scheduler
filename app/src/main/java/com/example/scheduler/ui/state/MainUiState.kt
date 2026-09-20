package com.example.scheduler.ui.state

import com.example.scheduler.data.model.Event

/**
 * Single source of truth for the Main Dashboard state.
 */
sealed interface MainUiState {
    object Loading : MainUiState
    data class Success(
        val events: List<Event>,
        val userName: String,
        val selectedFilter: String = "All Events"
    ) : MainUiState
    data class Error(val message: String) : MainUiState
}
