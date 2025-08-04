package com.example.githubuserlist.viewmodel

import com.example.githubuserlist.Constants.TOKEN
import com.example.githubuserlist.data.model.GitHubRepositoryRes
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.model.RepositoryOwner
import com.example.githubuserlist.data.repository.GitHubRepository
import com.example.githubuserlist.viewmodel.UserRepoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepoViewModelTest {
    
    private lateinit var viewModel: UserRepoViewModel
    private lateinit var mockRepository: GitHubRepository
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        viewModel = UserRepoViewModel(mockRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be Loading`() = runTest {
        val initialState = viewModel.uiState.first()
        assertTrue(initialState is UserRepoUiState.Loading)
    }
    
    @Test
    fun `loadUserData should update state to Success with user and repositories`() = runTest {
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
        
        whenever(mockRepository.getUser(username, TOKEN)).thenReturn(mockUser)
        whenever(mockRepository.getUserRepositories(username, token = TOKEN)).thenReturn(mockRepositories)
        
        // When
        viewModel.loadUserData(username)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is UserRepoUiState.Success)
        val successState = currentState as UserRepoUiState.Success
        assertEquals(mockUser, successState.user)
        assertEquals(mockRepositories, successState.repositories)
    }
    
    @Test
    fun `loadUserData should handle error and update state to Error`() = runTest {
        // Given
        val username = "testuser"
        val errorMessage = "User not found"
        whenever(mockRepository.getUser(username, TOKEN))
            .thenThrow(RuntimeException(errorMessage))
        
        // When
        viewModel.loadUserData(username)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is UserRepoUiState.Error)
        assertEquals(errorMessage, (currentState as UserRepoUiState.Error).message)
    }
    
    @Test
    fun `loadUserData should handle repository error`() = runTest {
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
        val errorMessage = "Failed to load repositories"
        
        whenever(mockRepository.getUser(username, TOKEN)).thenReturn(mockUser)
        whenever(mockRepository.getUserRepositories(username, token = TOKEN))
            .thenThrow(RuntimeException(errorMessage))
        
        // When
        viewModel.loadUserData(username)
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is UserRepoUiState.Error)
        assertEquals(errorMessage, (currentState as UserRepoUiState.Error).message)
    }
} 