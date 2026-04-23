package com.example.ramapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ramapp.data.db.entity.FilterStateEntity

@Dao
interface FilterStateDao {
    
    @Query("SELECT * FROM filter_state WHERE id = 1")
    suspend fun getFilterState(): FilterStateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFilterState(filterState: FilterStateEntity)
    
    @Query("DELETE FROM filter_state")
    suspend fun clearFilterState()
}
