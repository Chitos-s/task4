package com.example.ramapp.di

import android.content.Context
import androidx.room.Room
import com.example.ramapp.data.db.RamAppDatabase
import com.example.ramapp.data.db.dao.StoredCharacterDao
import com.example.ramapp.data.db.dao.FilterStateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): RamAppDatabase = Room.databaseBuilder(
        context,
        RamAppDatabase::class.java,
        "ram_app_database"
    ).build()
    
    @Singleton
    @Provides
    fun provideStoredCharacterDao(database: RamAppDatabase): StoredCharacterDao =
        database.storedCharacterDao()
    
    @Singleton
    @Provides
    fun provideFilterStateDao(database: RamAppDatabase): FilterStateDao =
        database.filterStateDao()
}
