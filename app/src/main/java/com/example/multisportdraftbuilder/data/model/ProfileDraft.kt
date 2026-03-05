package com.example.multisportdraftbuilder.data.model

data class ProfileDraft(
    val id: Int,
    val name: String,
    val disciplines: List<String>,
    val accent: String,
    val season: String,
    val skills: Map<String, Int>
)
