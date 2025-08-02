package com.example.githubuserlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserlist.TOKEN
import com.example.githubuserlist.data.model.GitHubRepositoryRes
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.repository.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRepoViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UserRepoUiState>(UserRepoUiState.Loading)
    val uiState: StateFlow<UserRepoUiState> = _uiState.asStateFlow()
    
    fun loadUserData(username: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UserRepoUiState.Loading
                
                // Load user details and repositories in parallel
                val (user, repositories) = coroutineScope {
                    val userDeferred = async {
                        repository.getUser(username, TOKEN)
                    }
                    
                    val reposDeferred = async {
                        repository.getUserRepositories(username, token = TOKEN)
                    }
                    
                    userDeferred.await() to reposDeferred.await()
                }
                
                _uiState.value = UserRepoUiState.Success(user, repositories)
                
            } catch (e: Exception) {
                _uiState.value = UserRepoUiState.Error(e.message ?: "Failed to load user data")
            }
        }
    }
}

public sealed class UserRepoUiState {
    object Loading : UserRepoUiState()
    data class Success(
        val user: GitHubUser,
        val repositories: List<GitHubRepositoryRes>
    ) : UserRepoUiState()
    data class Error(val message: String) : UserRepoUiState()
} 