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
        private const val STORAGE_DURATION_MS = 24 * 60 * 60 * 1000L // 24 hours
    }
    
    suspend fun getStoredCharacters(filters: CharacterFilters): List<Character> {
        val filterHash = generateFilterHash(filters)
        return storedCharacterDao.getStoredCharacters(filterHash).map { it.toCharacter() }
    }
    
    suspend fun getCharactersForPage(filters: CharacterFilters, page: Int): List<Character> {
        val filterHash = generateFilterHash(filters)
        return storedCharacterDao.getCharactersForPage(filterHash, page).map { it.toCharacter() }
    }

    suspend fun getMaxStoredPage(filters: CharacterFilters): Int {
        val filterHash = generateFilterHash(filters)
        return storedCharacterDao.getMaxStoredPage(filterHash)
    }

    suspend fun getStoredCharacter(id: Int, filters: CharacterFilters): Character? {
        val filterHash = generateFilterHash(filters)
        return storedCharacterDao.getStoredCharacter(filterHash, id)?.toCharacter()
    }

    suspend fun getStoredCharacterById(id: Int): Character? {
        return storedCharacterDao.getStoredCharacterById(id)?.toCharacter()
    }
    
    suspend fun saveCharacter(character: Character, filters: CharacterFilters, page: Int) {
        val filterHash = generateFilterHash(filters)
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
            filterHash = filterHash,
            page = page
        )
        storedCharacterDao.saveCharacter(entity)
    }
    
    suspend fun saveCharacters(characters: List<Character>, filters: CharacterFilters, page: Int) {
        val filterHash = generateFilterHash(filters)
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
                filterHash = filterHash,
                page = page
            )
        }
        storedCharacterDao.saveCharacters(entities)
    }
    
    suspend fun clearExpiredData() {
        val expirationTime = System.currentTimeMillis() - STORAGE_DURATION_MS
        storedCharacterDao.clearExpiredData(expirationTime)
    }
    
    suspend fun clearDataByFilter(filters: CharacterFilters) {
        val filterHash = generateFilterHash(filters)
        storedCharacterDao.clearDataByFilter(filterHash)
    }

    suspend fun clearAllData() {
        storedCharacterDao.clearAllData()
    }
    
    private fun generateFilterHash(filters: CharacterFilters): String {
        val parts = listOf(
            filters.name ?: "",
            filters.status ?: "",
            filters.species ?: "",
            filters.type ?: "",
            filters.gender ?: ""
        )
        return parts.joinToString("|").hashCode().toString()
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
