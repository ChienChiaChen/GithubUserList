package com.example.githubuserlist.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUser(
    val id: Int,
    val login: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val type: String,
    @SerialName("site_admin")
    val siteAdmin: Boolean,
    val name: String? = null,
    val company: String? = null,
    val blog: String? = null,
    val location: String? = null,
    val email: String? = null,
    val bio: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int? = null,
    @SerialName("public_gists")
    val publicGists: Int? = null,
    val followers: Int? = null,
    val following: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
) 