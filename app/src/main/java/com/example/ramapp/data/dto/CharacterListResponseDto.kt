package com.example.ramapp.data.dto

data class CharacterListResponse(
    val info: InfoDto,
    val results: List<CharacterDto>
)
