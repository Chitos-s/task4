package com.example.ramapp.data.repository

import com.example.ramapp.data.api.RickMortyApi
import com.example.ramapp.data.dto.CharacterDto
import com.example.ramapp.domain.model.Character
import com.example.ramapp.domain.model.CharacterFilters
import com.example.ramapp.domain.model.PaginationInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val api: RickMortyApi
) : CharacterRepository {
    override suspend fun getCharacters(page: Int, filters: CharacterFilters): Pair<List<Character>, PaginationInfo> {
        val response = api.getCharacters(
            page = page,
            name = filters.name,
            status = filters.status,
            species = filters.species,
            type = filters.type,
            gender = filters.gender
        )
        val paginationInfo = PaginationInfo(
            currentPage = page,
            totalPages = response.info.pages,
            hasNext = response.info.next != null,
            hasPrev = response.info.prev != null
        )
        return response.results.map { it.toDomain() } to paginationInfo
    }

    override suspend fun getCharacter(id: Int): Character {
        return api.getCharacter(id).toDomain()
    }

    private fun CharacterDto.toDomain(): Character {
        return Character(
            id = id,
            name = name,
            status = status,
            species = species,
            type = type,
            gender = gender,
            imageUrl = image,
            originName = origin.name,
            locationName = location.name,
            episodeCount = episode.size
        )
    }
}
