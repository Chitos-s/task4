package com.example.ramapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ramapp.data.db.entity.StoredCharacterEntity

@Dao
interface StoredCharacterDao {
    
    @Query("SELECT * FROM stored_characters WHERE filterHash = :filterHash ORDER BY storedAt DESC")
    suspend fun getStoredCharacters(filterHash: String): List<StoredCharacterEntity>
    
    @Query("SELECT * FROM stored_characters WHERE filterHash = :filterHash AND page = :page ORDER BY id ASC")
    suspend fun getCharactersForPage(filterHash: String, page: Int): List<StoredCharacterEntity>

    @Query("SELECT COALESCE(MAX(page), 0) FROM stored_characters WHERE filterHash = :filterHash")
    suspend fun getMaxStoredPage(filterHash: String): Int
    
    @Query("SELECT * FROM stored_characters WHERE filterHash = :filterHash AND id = :id")
    suspend fun getStoredCharacter(filterHash: String, id: Int): StoredCharacterEntity?

    @Query("SELECT * FROM stored_characters WHERE id = :id ORDER BY storedAt DESC LIMIT 1")
    suspend fun getStoredCharacterById(id: Int): StoredCharacterEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacter(character: StoredCharacterEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacters(characters: List<StoredCharacterEntity>)
    
    @Query("DELETE FROM stored_characters WHERE storedAt < :expirationTime")
    suspend fun clearExpiredData(expirationTime: Long)
    
    @Query("DELETE FROM stored_characters WHERE filterHash = :filterHash")
    suspend fun clearDataByFilter(filterHash: String)

    @Query("DELETE FROM stored_characters")
    suspend fun clearAllData()
}
