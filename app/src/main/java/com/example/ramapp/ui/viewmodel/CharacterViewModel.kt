package com.example.ramapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.ramapp.domain.model.CharacterFilters
import com.example.ramapp.domain.model.PaginationInfo
import com.example.ramapp.domain.usecase.GetCharactersUseCase
import com.example.ramapp.domain.usecase.GetCharacterDetailUseCase
import com.example.ramapp.data.service.CharacterStorageService
import com.example.ramapp.data.service.FilterStateService
import com.example.ramapp.ui.state.DetailUiState
import com.example.ramapp.ui.state.ListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val storageService: CharacterStorageService,
    private val filterStateService: FilterStateService
) : ViewModel() {
    var listState by mutableStateOf<ListUiState>(ListUiState.Loading)
        private set
    var detailState by mutableStateOf<DetailUiState>(DetailUiState.Loading)
        private set
    private var currentPage by mutableIntStateOf(1)
    private var totalPages by mutableIntStateOf(1)
    var selectedFilters by mutableStateOf(CharacterFilters())
        private set
    private var prefetchJob: Job? = null

    init {
        restoreState()
    }
    
    private fun restoreState() {
        viewModelScope.launch {
            val savedState = filterStateService.getFilterState()
            if (savedState != null) {
                selectedFilters = savedState.filters
                currentPage = savedState.page
                totalPages = savedState.totalPages.coerceAtLeast(1)
                loadCharacters(savedState.page)
            } else {
                loadCharacters()
            }
        }
    }

    fun loadCharacters(page: Int = 1, preferStorage: Boolean = true, forceRefresh: Boolean = false) {
        currentPage = page
        viewModelScope.launch {
            if (preferStorage && !forceRefresh) {
                val storedItems = storageService.getCharactersForPage(selectedFilters, page)
                if (storedItems.isNotEmpty()) {
                    listState = ListUiState.Content(
                        items = storedItems,
                        pagination = PaginationInfo(
                            currentPage = page,
                            totalPages = totalPages,
                            hasNext = page < totalPages,
                            hasPrev = page > 1
                        )
                    )
                    return@launch
                }
            }

            listState = ListUiState.Loading

            try {
                val (items, pagination) = getCharactersUseCase(page, selectedFilters)
                storageService.clearExpiredData()
                storageService.saveCharacters(items, selectedFilters, page)
                totalPages = pagination.totalPages.coerceAtLeast(1)
                filterStateService.saveFilterState(selectedFilters, page, totalPages)
                listState = ListUiState.Content(
                    items = items,
                    pagination = pagination.copy(totalPages = totalPages)
                )

                if (page == 1 && pagination.totalPages > 1) {
                    startPrefetchRemainingPages(
                        totalPages = pagination.totalPages,
                        filters = selectedFilters
                    )
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    totalPages = 1
                    filterStateService.saveFilterState(selectedFilters, page, totalPages)
                    listState = ListUiState.Content(
                        items = emptyList(),
                        pagination = PaginationInfo(
                            currentPage = page,
                            totalPages = 1,
                            hasNext = false,
                            hasPrev = false
                        )
                    )
                } else {
                    val storedItems = storageService.getCharactersForPage(selectedFilters, page)
                    if (storedItems.isNotEmpty()) {
                        listState = ListUiState.Content(
                            items = storedItems,
                            pagination = PaginationInfo(
                                currentPage = page,
                                totalPages = totalPages,
                                hasNext = page < totalPages,
                                hasPrev = page > 1
                            )
                        )
                    } else {
                        listState = ListUiState.Error(
                            "Не удалось загрузить список. Попробуйте еще раз."
                        )
                    }
                }
            } catch (e: Exception) {
                val storedItems = storageService.getCharactersForPage(selectedFilters, page)
                if (storedItems.isNotEmpty()) {
                    listState = ListUiState.Content(
                        items = storedItems,
                        pagination = PaginationInfo(
                            currentPage = page,
                            totalPages = totalPages,
                            hasNext = page < totalPages,
                            hasPrev = page > 1
                        )
                    )
                } else {
                    listState = ListUiState.Error(
                        "Не удалось загрузить список. Попробуйте еще раз."
                    )
                }
            }
        }
    }

    fun applyFilters(filters: CharacterFilters) {
        viewModelScope.launch {
            prefetchJob?.cancel()
            val previousFilters = selectedFilters
            if (previousFilters != filters) {
                storageService.clearAllData()
                totalPages = 1
            }
            selectedFilters = filters
            loadCharacters(1, preferStorage = false, forceRefresh = true)
        }
    }

    fun retryCurrentPage() {
        loadCharacters(currentPage, preferStorage = false, forceRefresh = true)
    }

    fun nextPage() {
        val targetPage = (currentPage + 1).coerceAtMost(totalPages)
        loadCharacters(targetPage)
    }

    fun prevPage() {
        if (currentPage > 1) {
            loadCharacters(currentPage - 1)
        }
    }

    fun loadCharacter(id: Int) {
        detailState = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val character = getCharacterDetailUseCase(id)
                detailState = DetailUiState.Content(character)
                storageService.saveCharacter(character, selectedFilters, currentPage)
            } catch (e: Exception) {
                val storedCharacter = storageService.getStoredCharacterById(id)
                if (storedCharacter != null) {
                    detailState = DetailUiState.Content(storedCharacter)
                } else {
                    detailState = DetailUiState.Error(
                        "Не удалось загрузить детали. Попробуйте еще раз."
                    )
                }
            }
        }
    }

    private fun startPrefetchRemainingPages(totalPages: Int, filters: CharacterFilters) {
        prefetchJob?.cancel()
        prefetchJob = viewModelScope.launch {
            for (page in 2..totalPages) {
                if (!isActive) return@launch
                if (selectedFilters != filters) return@launch
                try {
                    val (items, _) = getCharactersUseCase(page, filters)
                    storageService.saveCharacters(items, filters, page)
                    filterStateService.saveFilterState(filters, currentPage, totalPages)
                } catch (_: Exception) {
                    return@launch
                }
            }
        }
    }
}
