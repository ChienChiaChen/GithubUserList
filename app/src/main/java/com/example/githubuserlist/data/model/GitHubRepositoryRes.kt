package com.example.githubuserlist.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepositoryRes(
    val id: Int,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val description: String? = null,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("clone_url")
    val cloneUrl: String,
    @SerialName("git_url")
    val gitUrl: String,
    @SerialName("ssh_url")
    val sshUrl: String,
    @SerialName("svn_url")
    val svnUrl: String,
    val language: String? = null,
    @SerialName("default_branch")
    val defaultBranch: String,
    val fork: Boolean,
    @SerialName("forks_count")
    val forksCount: Int,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("watchers_count")
    val watchersCount: Int,
    @SerialName("open_issues_count")
    val openIssuesCount: Int,
    val size: Int,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("pushed_at")
    val pushedAt: String,
    val private: Boolean,
    val archived: Boolean,
    val disabled: Boolean,
    val license: License? = null,
    val owner: RepositoryOwner
)

@Serializable
data class License(
    val key: String,
    val name: String,
    @SerialName("spdx_id")
    val spdxId: String? = null,
    val url: String? = null,
    @SerialName("node_id")
    val nodeId: String
)

@Serializable
data class RepositoryOwner(
    val login: String,
    val id: Int,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val type: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean
) 