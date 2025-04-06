package org.example.model.github.model

data class RepositoryDto(
    val id: Double,
    val name: String,
    val fullName: String,
    val description: String?,
    val owner: String,
    val url: String,
    val additionalInfo: Map<String, Any> = emptyMap()
)
