package com.example.ramapp.data.service

import com.example.ramapp.data.db.dao.StoredCharacterDao
import com.example.ramapp.data.db.entity.StoredCharacterEntity
import com.example.ramapp.domain.model.Character
import com.example.ramapp.domain.model.CharacterFilters
import javax.inject.Inject

class CharacterStorageService @Inject constructor(
    private val storedCharacterDao: StoredCharacterDao
) {

    companion object {
        private const val STORAGE_DURATION_MS = 24 * 60 * 60 * 1000L
    }

    suspend fun getStoredCharacters(filters: CharacterFilters): List<Character> {
        clearExpiredData()
        val filterKey = generateFilterKey(filters)
        val expirationTime = System.currentTimeMillis() - STORAGE_DURATION_MS
        return storedCharacterDao.getStoredCharacters(filterKey)
            .filter { it.storedAt > expirationTime }
            .map { it.toCharacter() }
    }

    suspend fun getCharactersForPage(filters: CharacterFilters, page: Int): List<Character> {
        clearExpiredData()
        val filterKey = generateFilterKey(filters)
        val expirationTime = System.currentTimeMillis() - STORAGE_DURATION_MS
        return storedCharacterDao.getCharactersForPage(filterKey, page)
            .filter { it.storedAt > expirationTime }
            .map { it.toCharacter() }
    }

    suspend fun getMaxStoredPage(filters: CharacterFilters): Int {
        clearExpiredData()
        val filterKey = generateFilterKey(filters)
        return storedCharacterDao.getMaxStoredPage(filterKey)
    }

    suspend fun getStoredCharacter(id: Int, filters: CharacterFilters): Character? {
        clearExpiredData()
        val filterKey = generateFilterKey(filters)
        return storedCharacterDao.getStoredCharacter(filterKey, id)?.toCharacter()
    }

    suspend fun getStoredCharacterById(id: Int): Character? {
        clearExpiredData()
        return storedCharacterDao.getStoredCharacterById(id)?.toCharacter()
    }

    suspend fun saveCharacter(character: Character, filters: CharacterFilters, page: Int) {
        val filterKey = generateFilterKey(filters)
        val entity = StoredCharacterEntity(
            id = character.id,
            name = character.name,
            status = character.status,
            species = character.species,
            type = character.type,
            gender = character.gender,
            imageUrl = character.imageUrl,
            originName = character.originName,
            locationName = character.locationName,
            episodeCount = character.episodeCount,
            filterHash = filterKey,
            page = page,
            storedAt = System.currentTimeMillis()
        )
        storedCharacterDao.saveCharacter(entity)
    }

    suspend fun saveCharacters(characters: List<Character>, filters: CharacterFilters, page: Int) {
        val filterKey = generateFilterKey(filters)
        val currentTime = System.currentTimeMillis()
        val entities = characters.map { character ->
            StoredCharacterEntity(
                id = character.id,
                name = character.name,
                status = character.status,
                species = character.species,
                type = character.type,
                gender = character.gender,
                imageUrl = character.imageUrl,
                originName = character.originName,
                locationName = character.locationName,
                episodeCount = character.episodeCount,
                filterHash = filterKey,
                page = page,
                storedAt = currentTime
            )
        }
        storedCharacterDao.saveCharacters(entities)
    }

    suspend fun clearExpiredData() {
        val expirationTime = System.currentTimeMillis() - STORAGE_DURATION_MS
        storedCharacterDao.clearExpiredData(expirationTime)
    }

    suspend fun clearDataByFilter(filters: CharacterFilters) {
        val filterKey = generateFilterKey(filters)
        storedCharacterDao.clearDataByFilter(filterKey)
    }

    suspend fun clearAllData() {
        storedCharacterDao.clearAllData()
    }

    private fun generateFilterKey(filters: CharacterFilters): String {
        return listOf(
            "name=${filters.name.orEmpty().trim()}",
            "status=${filters.status.orEmpty().trim()}",
            "species=${filters.species.orEmpty().trim()}",
            "type=${filters.type.orEmpty().trim()}",
            "gender=${filters.gender.orEmpty().trim()}"
        ).joinToString("&")
    }

    private fun StoredCharacterEntity.toCharacter() = Character(
        id = id,
        name = name,
        status = status,
        species = species,
        type = type,
        gender = gender,
        imageUrl = imageUrl,
        originName = originName,
        locationName = locationName,
        episodeCount = episodeCount
    )
}
