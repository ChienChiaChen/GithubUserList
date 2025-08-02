package com.example.githubuserlist.data.api

import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.model.GitHubRepositoryRes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {
    
    /**
     * Get GitHub users list (for browsing)
     * @param since Start from user ID (for pagination)
     * @param perPage Number of users per page (max 100)
     * @param authorization GitHub Personal Access Token
     */
    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Int? = null,
        @Query("per_page") perPage: Int = 30,
        @Header("Authorization") authorization: String? = null
    ): List<GitHubUser>
    
    /**
     * Search GitHub users
     * @param query Search keyword
     * @param page Page number
     * @param perPage Number of users per page
     * @param authorization GitHub Personal Access Token
     */
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Header("Authorization") authorization: String? = null
    ): SearchResponse
    
    /**
     * Get detailed user information
     * @param username GitHub username
     * @param authorization GitHub Personal Access Token
     */
    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String,
        @Header("Authorization") authorization: String? = null
    ): GitHubUser
    
    /**
     * Get user's repositories (excluding forks)
     * @param username GitHub username
     * @param page Page number
     * @param perPage Number of repos per page
     * @param sort Sort by: created, updated, pushed, full_name
     * @param direction Sort direction: asc, desc
     * @param authorization GitHub Personal Access Token
     */
    @GET("users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "updated",
        @Query("direction") direction: String = "desc",
        @Query("type") type: String = "owner", // Only return repositories owned by the user
        @Header("Authorization") authorization: String? = null
    ): List<GitHubRepositoryRes>
}

@Serializable
data class SearchResponse(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<GitHubUser>
) 