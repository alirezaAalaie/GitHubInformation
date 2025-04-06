package org.example.model.database


import org.example.model.github.model.RepositoryDto
import org.litote.kmongo.Id
import org.litote.kmongo.newId

data class UserRepoEntity(
    val _id: Id<UserRepoEntity> = newId(),
    val login: String,
    val name: String?,
    val followers: Double,
    val following: Double,
    val createdAt: String,
    val publicRepos: Double,
    var repositories: List<RepositoryEntity> = emptyList(),
    val additionalInfo: Map<String, Any> = emptyMap()
)

data class RepositoryEntity(
    val id: Double,
    val name: String,
    val fullName: String,
    val description: String?,
    val url: String,
    val additionalInfo: Map<String, Any> = emptyMap()
) {
    fun toDto(owner: String): RepositoryDto = RepositoryDto(id = id, name = name, fullName =  fullName, description =  description, url =  url, owner =  owner, additionalInfo =  additionalInfo)
}