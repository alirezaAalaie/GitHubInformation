package org.example.model.github

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {
    @GET("/users/{username}")
    suspend fun getUser(@Path("username") username: String): Map<String, Any>

    @GET("/users/{username}/repos")
    suspend fun getUserRepos(@Path("username") username: String): List<Map<String, Any>>
}