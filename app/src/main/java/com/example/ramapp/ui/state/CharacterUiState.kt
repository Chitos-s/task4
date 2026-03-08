package com.example.ramapp.ui.state

import com.example.ramapp.domain.model.Character
import com.example.ramapp.domain.model.PaginationInfo

sealed interface ListUiState {
    data object Loading : ListUiState
    data class Content(
        val items: List<Character>,
        val pagination: PaginationInfo
    ) : ListUiState
    data class Error(val message: String) : ListUiState
}

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Content(val character: Character) : DetailUiState
    data class Error(val message: String) : DetailUiState
}
