package com.example.ramapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "filter_state")
data class FilterStateEntity(
    @PrimaryKey
    val id: Int = 1, // Всегда одна запись с текущим состоянием фильтров
    val name: String?,
    val status: String?,
    val species: String?,
    val type: String?,
    val gender: String?,
    val lastPage: Int = 1,
    val totalPages: Int = 1,
    val updatedAt: Long = System.currentTimeMillis()
)
