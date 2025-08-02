package com.example.githubuserlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserlist.TOKEN
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GitHubUsersViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<GitHubUsersUiState>(GitHubUsersUiState.Loading)
    val uiState: StateFlow<GitHubUsersUiState> = _uiState.asStateFlow()
    
    private val _users = MutableStateFlow<List<GitHubUser>>(emptyList())
    val users: StateFlow<List<GitHubUser>> = _users.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private var currentPage = 0
    private var isLoading = false
    private var hasMoreData = true
    private var searchJob: Job? = null

    init {
        loadUsers()
        setupSearchDebounce()
    }
    
    private fun setupSearchDebounce() {
        _searchQuery
            .debounce(500) // Wait 500ms after user stops typing
            .distinctUntilChanged() // Only emit if value changed
            .filter { it.isNotBlank() } // Only search non-empty queries
            .onEach { query ->
                // Show loading state
                _uiState.value = GitHubUsersUiState.Loading
            }
            .flatMapLatest { query ->
                // Perform search
                kotlinx.coroutines.flow.flow {
                    try {
                        val searchResponse = repository.searchUsers(query = query, token = TOKEN)
                        emit(searchResponse.items)
                    } catch (e: Exception) {
                        throw e
                    }
                }
            }
            .onEach { users ->
                _users.value = users
                _uiState.value = GitHubUsersUiState.Success(users)
            }
            .launchIn(viewModelScope)
    }

    fun loadUsers(refresh: Boolean = false) {
        if (isLoading || (!refresh && !hasMoreData)) return
        
        viewModelScope.launch {
            try {
                isLoading = true
                _uiState.value = GitHubUsersUiState.Loading
                
                val since = if (refresh) null else currentPage
                val newUsers = repository.getUsers(since = since, token = TOKEN)
                
                if (refresh) {
                    _users.value = newUsers
                    currentPage = newUsers.lastOrNull()?.id ?: 0
                } else {
                    _users.value = _users.value + newUsers
                    currentPage = newUsers.lastOrNull()?.id ?: currentPage
                }
                
                hasMoreData = newUsers.isNotEmpty()
                _uiState.value = GitHubUsersUiState.Success(_users.value)
                
            } catch (e: Exception) {
                _uiState.value = GitHubUsersUiState.Error(e.message ?: "Unknown error")
            } finally {
                isLoading = false
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        
        // If query is empty, load all users
        if (query.isBlank()) {
            loadUsers(refresh = true)
        }
    }
}

public sealed class GitHubUsersUiState {
    object Loading : GitHubUsersUiState()
    data class Success(val users: List<GitHubUser>) : GitHubUsersUiState()
    data class Error(val message: String) : GitHubUsersUiState()
} 