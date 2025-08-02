package com.example.githubuserlist.data.repository

import com.example.githubuserlist.data.api.SearchResponse
import com.example.githubuserlist.data.model.GitHubRepositoryRes
import com.example.githubuserlist.data.model.GitHubUser

interface GitHubRepository {
    
    /**
     * Get GitHub users list
     * @param since Start from user ID (for pagination)
     * @param perPage Number of users per page
     * @param token GitHub Personal Access Token (optional)
     */
    suspend fun getUsers(
        since: Int? = null,
        perPage: Int = 30,
        token: String? = null
    ): List<GitHubUser>
    
    /**
     * Search GitHub users
     * @param query Search keyword
     * @param page Page number
     * @param perPage Number of users per page
     * @param token GitHub Personal Access Token (optional)
     */
    suspend fun searchUsers(
        query: String,
        page: Int = 1,
        perPage: Int = 30,
        token: String? = null
    ): SearchResponse
    
    /**
     * Get detailed user information
     * @param username GitHub username
     * @param token GitHub Personal Access Token (optional)
     */
    suspend fun getUser(
        username: String,
        token: String? = null
    ): GitHubUser
    
    /**
     * Get user's repositories (excluding forks)
     * @param username GitHub username
     * @param page Page number
     * @param perPage Number of repos per page
     * @param token GitHub Personal Access Token (optional)
     */
    suspend fun getUserRepositories(
        username: String,
        page: Int = 1,
        perPage: Int = 30,
        token: String? = null
    ): List<GitHubRepositoryRes>
} 