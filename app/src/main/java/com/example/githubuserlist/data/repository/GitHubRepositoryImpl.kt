package com.example.githubuserlist.data.repository

import com.example.githubuserlist.data.api.GitHubApiService
import com.example.githubuserlist.data.api.SearchResponse
import com.example.githubuserlist.data.model.GitHubRepositoryRes
import com.example.githubuserlist.data.model.GitHubUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GitHubRepositoryImpl @Inject constructor(
    private val apiService: GitHubApiService
) : GitHubRepository {

    override suspend fun getUsers(
        since: Int?,
        perPage: Int,
        token: String?
    ): List<GitHubUser> = withContext(Dispatchers.IO) {
        try {
            val authorization = token?.let { "Bearer $it" }
            apiService.getUsers(since, perPage, authorization)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun searchUsers(
        query: String,
        page: Int,
        perPage: Int,
        token: String?
    ): SearchResponse = withContext(Dispatchers.IO) {
        try {
            val authorization = token?.let { "Bearer $it" }
            apiService.searchUsers(query, page, perPage, authorization)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUser(
        username: String,
        token: String?
    ): GitHubUser = withContext(Dispatchers.IO) {
        try {
            val authorization = token?.let { "Bearer $it" }
            apiService.getUser(username, authorization)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUserRepositories(
        username: String,
        page: Int,
        perPage: Int,
        token: String?
    ): List<GitHubRepositoryRes> = withContext(Dispatchers.IO) {
        try {
            val authorization = token?.let { "Bearer $it" }
            apiService.getUserRepositories(
                username = username,
                page = page,
                perPage = perPage,
                sort = "updated",
                direction = "desc",
                type = "owner",
                authorization = authorization
            )
        } catch (e: Exception) {
            throw e
        }
    }
} 