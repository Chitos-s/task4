package com.example.ramapp.domain.usecase

import com.example.ramapp.data.repository.CharacterRepository
import com.example.ramapp.domain.model.Character
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Character {
        return repository.getCharacter(id)
    }
}