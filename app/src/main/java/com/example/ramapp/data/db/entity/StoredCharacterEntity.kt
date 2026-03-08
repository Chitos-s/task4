package com.example.ramapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stored_characters")
data class StoredCharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val imageUrl: String,
    val originName: String,
    val locationName: String,
    val episodeCount: Int,
    val filterHash: String,
    val page: Int, // Номер страницы, на которой был этот персонаж
    val storedAt: Long = System.currentTimeMillis()
)
