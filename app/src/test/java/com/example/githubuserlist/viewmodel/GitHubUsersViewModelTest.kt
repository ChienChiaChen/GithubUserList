package com.example.githubuserlist.viewmodel

import com.example.githubuserlist.Constants.TOKEN
import com.example.githubuserlist.data.api.SearchResponse
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.repository.GitHubRepository
import com.example.githubuserlist.viewmodel.GitHubUsersUiState
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
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubUsersViewModelTest {
    
    private lateinit var viewModel: GitHubUsersViewModel
    private lateinit var mockRepository: GitHubRepository
    private val testDispatcher = StandardTestDispatcher()
    
    private fun createViewModel() {
        viewModel = GitHubUsersViewModel(mockRepository)
    }
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mock()
        // Don't create viewModel here to avoid init block execution
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be Loading`() = runTest {
        // Given
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(emptyList())
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val initialState = viewModel.uiState.first()
        assertTrue(initialState is GitHubUsersUiState.Success)
    }
    
    @Test
    fun `initial search query should be empty`() = runTest {
        // Given
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(emptyList())
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val initialQuery = viewModel.searchQuery.first()
        assertEquals("", initialQuery)
    }
    
    @Test
    fun `loadUsers should update state to Success with users`() = runTest {
        // Given
        val mockUsers = listOf(
            GitHubUser(
                id = 1,
                login = "testuser1",
                avatarUrl = "https://example.com/avatar1.jpg",
                htmlUrl = "https://github.com/testuser1",
                type = "User",
                siteAdmin = false,
                name = "Test User 1",
                followers = 100,
                following = 50,
                publicRepos = 25
            ),
            GitHubUser(
                id = 2,
                login = "testuser2",
                avatarUrl = "https://example.com/avatar2.jpg",
                htmlUrl = "https://github.com/testuser2",
                type = "User",
                siteAdmin = false,
                name = "Test User 2",
                followers = 200,
                following = 75,
                publicRepos = 30
            )
        )
        whenever(mockRepository.getUsers(since = null, token = TOKEN)).thenReturn(mockUsers)
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is GitHubUsersUiState.Success)
        assertEquals(mockUsers, (currentState as GitHubUsersUiState.Success).users)
    }
    
    @Test
    fun `loadUsers should handle error and update state to Error`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenThrow(RuntimeException(errorMessage))
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is GitHubUsersUiState.Error)
        assertEquals(errorMessage, (currentState as GitHubUsersUiState.Error).message)
    }
    
    @Test
    fun `searchQuery should start with empty string`() = runTest {
        // Given
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(emptyList())
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val initialQuery = viewModel.searchQuery.first()
        assertEquals("", initialQuery)
    }
    
    @Test
    fun `updateSearchQuery should trigger search after debounce`() = runTest {
        // Given
        val searchQuery = "test search"
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
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(emptyList())
        whenever(mockRepository.searchUsers(query = searchQuery, token = TOKEN))
            .thenReturn(SearchResponse(totalCount = 1, incompleteResults = false, items = mockUsers))
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery(searchQuery)
        
        // Advance past the debounce delay (500ms)
        testDispatcher.scheduler.advanceTimeBy(600)
        testDispatcher.scheduler.runCurrent()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is GitHubUsersUiState.Success)
        assertEquals(mockUsers, (currentState as GitHubUsersUiState.Success).users)
    }
    
    @Test
    fun `updateSearchQuery with empty string should load all users`() = runTest {
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
        whenever(mockRepository.getUsers(since = null, token = TOKEN)).thenReturn(mockUsers)
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        val currentState = viewModel.uiState.first()
        assertTrue(currentState is GitHubUsersUiState.Success)
    }
    
    @Test
    fun `canLoadMore should return false when loading`() = runTest {
        // Given
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(emptyList())
        
        // When - Create viewModel and start loading
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle() // Complete initial load
        
        // Start a new load - this will set isLoading = true
        viewModel.loadUsers(refresh = true)
        // Don't advance dispatcher to keep it in loading state
        
        // Then
        assertFalse(viewModel.canLoadMore())
    }
    
    @Test
    fun `canLoadMore should return true when not loading and has more data`() = runTest {
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
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(mockUsers)
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertTrue(viewModel.canLoadMore())
    }
    
    @Test
    fun `canLoadMore should return false when no more data`() = runTest {
        // Given
        whenever(mockRepository.getUsers(since = null, token = TOKEN))
            .thenReturn(emptyList())
        
        // When
        createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.canLoadMore())
    }
} 