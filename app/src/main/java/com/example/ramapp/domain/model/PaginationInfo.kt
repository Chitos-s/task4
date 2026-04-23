package com.example.ramapp.domain.model

data class PaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean
)
