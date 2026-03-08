package com.example.ramapp.data.service

import com.example.ramapp.data.db.dao.FilterStateDao
import com.example.ramapp.data.db.entity.FilterStateEntity
import com.example.ramapp.domain.model.CharacterFilters
import javax.inject.Inject

class FilterStateService @Inject constructor(
    private val filterStateDao: FilterStateDao
) {
    
    suspend fun saveFilterState(filters: CharacterFilters, page: Int, totalPages: Int = 1) {
        val entity = FilterStateEntity(
            id = 1,
            name = filters.name,
            status = filters.status,
            species = filters.species,
            type = filters.type,
            gender = filters.gender,
            lastPage = page,
            totalPages = totalPages
        )
        filterStateDao.saveFilterState(entity)
    }
    
    data class SavedState(
        val filters: CharacterFilters,
        val page: Int,
        val totalPages: Int
    )
    
    suspend fun getFilterState(): SavedState? {
        val entity = filterStateDao.getFilterState() ?: return null
        val filters = CharacterFilters(
            name = entity.name,
            status = entity.status,
            species = entity.species,
            type = entity.type,
            gender = entity.gender
        )
        return SavedState(filters, entity.lastPage, entity.totalPages)
    }
    
    suspend fun clearFilterState() {
        filterStateDao.clearFilterState()
    }
}
