package com.example.ramapp.data.repository

import com.example.ramapp.domain.model.CharacterFilters
import com.example.ramapp.domain.model.Character
import com.example.ramapp.domain.model.PaginationInfo

interface CharacterRepository {
    suspend fun getCharacters(page: Int, filters: CharacterFilters): Pair<List<Character>, PaginationInfo>
    suspend fun getCharacter(id: Int): Character
}
