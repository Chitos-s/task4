package com.example.ramapp.di

import com.example.ramapp.data.api.RickMortyApi
import com.example.ramapp.data.repository.CharacterRepository
import com.example.ramapp.data.repository.CharacterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideCharacterRepository(api: RickMortyApi): CharacterRepository {
        return CharacterRepositoryImpl(api)
    }
}
