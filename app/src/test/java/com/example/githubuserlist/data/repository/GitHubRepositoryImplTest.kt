package com.example.githubuserlist.data.repository

import com.example.githubuserlist.Constants.TOKEN
import com.example.githubuserlist.data.api.GitHubApiService
import com.example.githubuserlist.data.api.SearchResponse
import com.example.githubuserlist.data.model.GitHubRepositoryRes
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.model.RepositoryOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoryImplTest {
    
    private lateinit var repository: GitHubRepositoryImpl
    private lateinit var mockApiService: GitHubApiService
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApiService = mock()
        repository = GitHubRepositoryImpl(mockApiService)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `getUsers should return users from API service`() = runTest {
        // Given
        val mockUsers = listOf(
            GitHubUser(
                id = 1,
                login = "testuser",
                avatarUrl = "https://example.com/avatar.jpg",
                htmlUrl = "https://github.com/testuser",
                type = "User",
                siteAdmin = false,
                name = "Test User",
                followers = 100,
                following = 50,
                publicRepos = 25
            )
        )
        whenever(mockApiService.getUsers(since = null, perPage = 30, authorization = "Bearer $TOKEN"))
            .thenReturn(mockUsers)
        
        // When
        val result = repository.getUsers(token = TOKEN)
        
        // Then
        assertEquals(mockUsers, result)
    }
    
    @Test
    fun `searchUsers should return search response from API service`() = runTest {
        // Given
        val query = "test"
        val mockUsers = listOf(
            GitHubUser(
                id = 1,
                login = "testuser",
                avatarUrl = "https://example.com/avatar.jpg",
                htmlUrl = "https://github.com/testuser",
                type = "User",
                siteAdmin = false,
                name = "Test User",
                followers = 100,
                following = 50,
                publicRepos = 25
            )
        )
        val mockSearchResponse = SearchResponse(
            totalCount = 1,
            incompleteResults = false,
            items = mockUsers
        )
        whenever(mockApiService.searchUsers(
            query = query,
            page = 1,
            perPage = 30,
            authorization = "Bearer $TOKEN"
        )).thenReturn(mockSearchResponse)
        
        // When
        val result = repository.searchUsers(query = query, token = TOKEN)
        
        // Then
        assertEquals(mockSearchResponse, result)
    }
    
    @Test
    fun `getUser should return user from API service`() = runTest {
        // Given
        val username = "testuser"
        val mockUser = GitHubUser(
            id = 1,
            login = username,
            avatarUrl = "https://example.com/avatar.jpg",
            htmlUrl = "https://github.com/$username",
            type = "User",
            siteAdmin = false,
            name = "Test User",
            followers = 100,
            following = 50,
            publicRepos = 25
        )
        whenever(mockApiService.getUser(
            username = username,
            authorization = "Bearer $TOKEN"
        )).thenReturn(mockUser)
        
        // When
        val result = repository.getUser(username = username, token = TOKEN)
        
        // Then
        assertEquals(mockUser, result)
    }
    
    @Test
    fun `getUserRepositories should return repositories from API service`() = runTest {
        // Given
        val username = "testuser"
        val mockRepositories = listOf(
            GitHubRepositoryRes(
                id = 1,
                name = "test-repo",
                fullName = "testuser/test-repo",
                description = "Test repository",
                htmlUrl = "https://github.com/testuser/test-repo",
                cloneUrl = "https://github.com/testuser/test-repo.git",
                gitUrl = "git://github.com/testuser/test-repo.git",
                sshUrl = "git@github.com:testuser/test-repo.git",
                svnUrl = "https://svn.github.com/testuser/test-repo",
                language = "Kotlin",
                defaultBranch = "main",
                fork = false,
                forksCount = 0,
                stargazersCount = 10,
                watchersCount = 10,
                openIssuesCount = 0,
                size = 100,
                createdAt = "2023-01-01T00:00:00Z",
                updatedAt = "2023-01-01T00:00:00Z",
                pushedAt = "2023-01-01T00:00:00Z",
                private = false,
                archived = false,
                disabled = false,
                owner = RepositoryOwner(
                    login = "testuser",
                    id = 1,
                    avatarUrl = "https://example.com/avatar.jpg",
                    htmlUrl = "https://github.com/testuser",
                    type = "User",
                    siteAdmin = false
                )
            )
        )
        whenever(mockApiService.getUserRepositories(
            username = username,
            page = 1,
            perPage = 30,
            sort = "updated",
            direction = "desc",
            type = "owner",
            authorization = "Bearer $TOKEN"
        )).thenReturn(mockRepositories)
        
        // When
        val result = repository.getUserRepositories(username = username, token = TOKEN)
        
        // Then
        assertEquals(mockRepositories, result)
    }
    
    @Test
    fun `getUsers should handle pagination with since parameter`() = runTest {
        // Given
        val since = 100
        val mockUsers = listOf(
            GitHubUser(
                id = 101,
                login = "testuser",
                avatarUrl = "https://example.com/avatar.jpg",
                htmlUrl = "https://github.com/testuser",
                type = "User",
                siteAdmin = false,
                name = "Test User",
                followers = 100,
                following = 50,
                publicRepos = 25
            )
        )
        whenever(mockApiService.getUsers(since = since, perPage = 30, authorization = "Bearer $TOKEN"))
            .thenReturn(mockUsers)
        
        // When
        val result = repository.getUsers(since = since, token = TOKEN)
        
        // Then
        assertEquals(mockUsers, result)
    }
} 