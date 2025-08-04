package com.example.githubuserlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserlist.Constants.TOKEN
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    
    // Pagination state
    private var lastUserId: Int? = null
    private var isLoading = false
    private var hasMoreData = true
    private var isSearchMode = false

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
                // Reset pagination state for search
                isSearchMode = true
                hasMoreData = true
                lastUserId = null
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
                
                if (refresh) {
                    _uiState.value = GitHubUsersUiState.Loading
                    lastUserId = null
                    hasMoreData = true
                    isSearchMode = false
                }
                
                val since = if (refresh) null else lastUserId
                val newUsers = repository.getUsers(since = since, token = TOKEN)
                
                if (refresh) {
                    _users.value = newUsers
                } else {
                    _users.value = _users.value + newUsers
                }
                
                // Update pagination state
                lastUserId = newUsers.lastOrNull()?.id
                hasMoreData = newUsers.isNotEmpty()
                
                _uiState.value = GitHubUsersUiState.Success(_users.value)
                
            } catch (e: Exception) {
                _uiState.value = GitHubUsersUiState.Error(e.message ?: "Unknown error")
            } finally {
                isLoading = false
            }
        }
    }
    
    fun loadMore() {
        if (!isSearchMode) {
            loadUsers(refresh = false)
        }
        // Note: Search API doesn't support pagination in this implementation
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        
        // If query is empty, load all users
        if (query.isBlank()) {
            isSearchMode = false
            loadUsers(refresh = true)
        }
    }
    
    fun canLoadMore(): Boolean {
        return !isLoading && hasMoreData && !isSearchMode
    }
}

public sealed class GitHubUsersUiState {
    object Loading : GitHubUsersUiState()
    data class Success(val users: List<GitHubUser>) : GitHubUsersUiState()
    data class Error(val message: String) : GitHubUsersUiState()
} 