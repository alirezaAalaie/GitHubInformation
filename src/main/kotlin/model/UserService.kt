package org.example.model

import org.example.model.database.RepositoryEntity
import org.example.model.database.UserRepoEntity
import org.example.model.github.client.GitHubClient
import org.example.model.github.model.RepositoryDto
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserService(private val db: CoroutineDatabase) {

    private val userCollection = db.getCollection<UserRepoEntity>()

    suspend fun getUser(username: String): UserRepoEntity {
        val cachedUser = userCollection.findOne(UserRepoEntity::login eq username)
        return if (cachedUser != null) {
            cachedUser
        } else {
            val userResponse = GitHubClient.apiService.getUser(username)
            val reposResponse = GitHubClient.apiService.getUserRepos(username)

            val user = UserRepoEntity(
                login = userResponse["login"] as String,
                name = userResponse["name"] as? String,
                followers = userResponse["followers"] as Double,
                following = userResponse["following"] as Double,
                createdAt = userResponse["created_at"] as String,
                publicRepos = userResponse["public_repos"] as Double,
                additionalInfo = userResponse.filterKeys {
                    it !in listOf(
                        "login",
                        "name",
                        "followers",
                        "following",
                        "created_at",
                        "public_repos"
                    )
                }
            )

            val repositories = reposResponse.map {
                RepositoryEntity(
                    id = it["id"] as Double,
                    name = it["name"] as String,
                    fullName = it["full_name"] as String,
                    description = it["description"] as? String,
                    url = it["html_url"] as String,
                    additionalInfo = it.filterKeys { key ->
                        key !in listOf(
                            "id",
                            "name",
                            "full_name",
                            "description",
                            "html_url"
                        )
                    }
                )
            }
            user.repositories = repositories
            userCollection.findOneAndUpdate(UserRepoEntity::login eq user.login, user) ?: userCollection.insertOne(user)
            user
        }
    }

    suspend fun getAllUsers(): List<UserRepoEntity> {
        return userCollection.find().toList()
    }

    suspend fun searchUserByUsername(username: String): UserRepoEntity? {
        return userCollection.findOne(UserRepoEntity::login eq username)
    }

    suspend fun searchRepositoryByName(repoName: String): List<RepositoryDto> {
        return userCollection.find().toList().flatMap { userEntity ->
            userEntity.repositories.filter { it.name.contains(repoName) }.map { it.toDto(userEntity.login) }
        }
    }
}