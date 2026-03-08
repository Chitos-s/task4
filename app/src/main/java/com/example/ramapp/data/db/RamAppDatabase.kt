package com.example.ramapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ramapp.data.db.dao.StoredCharacterDao
import com.example.ramapp.data.db.dao.FilterStateDao
import com.example.ramapp.data.db.entity.StoredCharacterEntity
import com.example.ramapp.data.db.entity.FilterStateEntity

@Database(
    entities = [StoredCharacterEntity::class, FilterStateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RamAppDatabase : RoomDatabase() {
    abstract fun storedCharacterDao(): StoredCharacterDao
    abstract fun filterStateDao(): FilterStateDao
}
