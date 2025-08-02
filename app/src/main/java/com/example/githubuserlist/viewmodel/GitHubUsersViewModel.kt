package com.example.githubuserlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserlist.TOKEN
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    
    private var currentPage = 0
    private var isLoading = false
    private var hasMoreData = true
    
    init {
        loadUsers()
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
    
    fun searchUsers(query: String) {
        if (query.isBlank()) {
            loadUsers(refresh = true)
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = GitHubUsersUiState.Loading

                val searchResponse = repository.searchUsers(query = query, token = TOKEN)
                _users.value = searchResponse.items
                _uiState.value = GitHubUsersUiState.Success(searchResponse.items)
                
            } catch (e: Exception) {
                _uiState.value = GitHubUsersUiState.Error(e.message ?: "Search failed")
            }
        }
    }
}

public sealed class GitHubUsersUiState {
    object Loading : GitHubUsersUiState()
    data class Success(val users: List<GitHubUser>) : GitHubUsersUiState()
    data class Error(val message: String) : GitHubUsersUiState()
} 