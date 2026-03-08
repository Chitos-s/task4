package com.example.ramapp.domain.usecase

import com.example.ramapp.data.repository.CharacterRepository
import com.example.ramapp.domain.model.Character
import com.example.ramapp.domain.model.CharacterFilters
import com.example.ramapp.domain.model.PaginationInfo
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(page: Int, filters: CharacterFilters): Pair<List<Character>, PaginationInfo> {
        return repository.getCharacters(page, filters)
    }
}