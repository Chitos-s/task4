package com.example.ramapp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ramapp.data.service.CharacterStorageService
import com.example.ramapp.data.service.FilterStateService
import com.example.ramapp.domain.model.CharacterFilters
import com.example.ramapp.domain.model.PaginationInfo
import com.example.ramapp.domain.usecase.GetCharacterDetailUseCase
import com.example.ramapp.domain.usecase.GetCharactersUseCase
import com.example.ramapp.ui.state.DetailUiState
import com.example.ramapp.ui.state.ListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
    private var currentRequestJob: Job? = null

    init {
        restoreState()
    }

    private fun restoreState() {
        viewModelScope.launch {
            val savedState = filterStateService.getFilterState()
            if (savedState != null) {
                selectedFilters = normalizeFilters(savedState.filters)
                currentPage = savedState.page
                totalPages = savedState.totalPages.coerceAtLeast(1)
                loadCharacters(savedState.page)
            } else {
                selectedFilters = CharacterFilters()
                currentPage = 1
                totalPages = 1
                loadCharacters()
            }
        }
    }

    fun loadCharacters(page: Int = 1, preferStorage: Boolean = true, forceRefresh: Boolean = false) {
        currentPage = page
        currentRequestJob?.cancel()
        val filterSnapshot = normalizeFilters(selectedFilters)
        selectedFilters = filterSnapshot

        currentRequestJob = viewModelScope.launch {
            val storedItems = if (preferStorage && !forceRefresh) {
                storageService.getCharactersForPage(filterSnapshot, page)
            } else {
                emptyList()
            }

            if (storedItems.isNotEmpty()) {
                val storedTotalPages = storageService.getMaxStoredPage(filterSnapshot)
                    .coerceAtLeast(totalPages)
                    .coerceAtLeast(1)
                totalPages = storedTotalPages
                listState = ListUiState.Content(
                    items = storedItems,
                    pagination = PaginationInfo(
                        currentPage = page,
                        totalPages = storedTotalPages,
                        hasNext = page < storedTotalPages,
                        hasPrev = page > 1
                    )
                )
            } else {
                listState = ListUiState.Loading
            }

            try {
                val (items, pagination) = getCharactersUseCase(page, filterSnapshot)
                storageService.clearExpiredData()
                storageService.saveCharacters(items, filterSnapshot, page)
                totalPages = pagination.totalPages.coerceAtLeast(1)
                if (hasActiveFilters(filterSnapshot)) {
                    filterStateService.saveFilterState(filterSnapshot, page, totalPages)
                } else {
                    filterStateService.clearFilterState()
                }
                listState = ListUiState.Content(
                    items = items,
                    pagination = pagination.copy(totalPages = totalPages)
                )
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    totalPages = 1
                    listState = if (storedItems.isNotEmpty()) {
                        ListUiState.Content(
                            items = storedItems,
                            pagination = PaginationInfo(
                                currentPage = page,
                                totalPages = totalPages,
                                hasNext = false,
                                hasPrev = page > 1
                            )
                        )
                    } else {
                        ListUiState.Empty
                    }
                } else {
                    if (storedItems.isEmpty()) {
                        showCachedPageOrError(filterSnapshot, page)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (storedItems.isEmpty()) {
                    showCachedPageOrError(filterSnapshot, page)
                }
            }
        }
    }

    fun applyFilters(filters: CharacterFilters) {
        viewModelScope.launch {
            val normalizedFilters = normalizeFilters(filters)
            selectedFilters = normalizedFilters
            currentPage = 1
            totalPages = storageService.getMaxStoredPage(normalizedFilters).coerceAtLeast(1)
            if (!hasActiveFilters(normalizedFilters)) {
                filterStateService.clearFilterState()
            }
            loadCharacters(1, preferStorage = true, forceRefresh = false)
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
            } catch (e: CancellationException) {
                throw e
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

    private suspend fun showCachedPageOrError(filters: CharacterFilters, page: Int) {
        val storedItems = storageService.getCharactersForPage(filters, page)
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

    private fun normalizeFilters(filters: CharacterFilters): CharacterFilters {
        fun normalize(value: String?): String? = value?.trim()?.takeIf { it.isNotEmpty() }

        return CharacterFilters(
            name = normalize(filters.name),
            status = normalize(filters.status),
            species = normalize(filters.species),
            type = normalize(filters.type),
            gender = normalize(filters.gender)
        )
    }

    private fun hasActiveFilters(filters: CharacterFilters): Boolean {
        return filters.name != null ||
            filters.status != null ||
            filters.species != null ||
            filters.type != null ||
            filters.gender != null
    }
}
