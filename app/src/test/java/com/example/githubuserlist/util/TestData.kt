package com.example.githubuserlist.util

import com.example.githubuserlist.data.model.GitHubRepositoryRes
import com.example.githubuserlist.data.model.GitHubUser
import com.example.githubuserlist.data.model.RepositoryOwner

object TestData {
    
    fun createMockUser(
        id: Int = 1,
        login: String = "testuser",
        avatarUrl: String = "https://example.com/avatar.jpg",
        name: String = "Test User",
        followers: Int = 100,
        following: Int = 50,
        publicRepos: Int = 25
    ): GitHubUser {
        return GitHubUser(
            id = id,
            login = login,
            avatarUrl = avatarUrl,
            htmlUrl = "https://github.com/$login",
            type = "User",
            siteAdmin = false,
            name = name,
            followers = followers,
            following = following,
            publicRepos = publicRepos
        )
    }
    
    fun createMockRepository(
        id: Int = 1,
        name: String = "test-repo",
        fullName: String = "testuser/test-repo",
        description: String = "Test repository",
        language: String = "Kotlin",
        stargazersCount: Int = 10,
        htmlUrl: String = "https://github.com/testuser/test-repo",
        fork: Boolean = false
    ): GitHubRepositoryRes {
        return GitHubRepositoryRes(
            id = id,
            name = name,
            fullName = fullName,
            description = description,
            htmlUrl = htmlUrl,
            cloneUrl = "https://github.com/testuser/test-repo.git",
            gitUrl = "git://github.com/testuser/test-repo.git",
            sshUrl = "git@github.com:testuser/test-repo.git",
            svnUrl = "https://svn.github.com/testuser/test-repo",
            language = language,
            defaultBranch = "main",
            fork = fork,
            forksCount = 0,
            stargazersCount = stargazersCount,
            watchersCount = stargazersCount,
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
    }
    
    fun createMockUsers(count: Int): List<GitHubUser> {
        return (1..count).map { index ->
            createMockUser(
                id = index,
                login = "user$index",
                name = "User $index"
            )
        }
    }
    
    fun createMockRepositories(count: Int): List<GitHubRepositoryRes> {
        return (1..count).map { index ->
            createMockRepository(
                id = index,
                name = "repo$index",
                fullName = "user/repo$index"
            )
        }
    }
} 