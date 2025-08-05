# GitHub User List Android App

## Overview

GitHub User List is an Android application built using Kotlin, Jetpack Compose, and modern Android development best practices. It demonstrates fetching and displaying GitHub user information and repositories using GitHub's API.

## Features

* Browse a paginated list of GitHub users.
* Search users by username.
* View user profile details.
* List repositories for selected GitHub users.
* Clean and intuitive UI built with Jetpack Compose.
* Dependency Injection using Dagger Hilt.
* Retrofit for networking.

## Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM
* **Networking:** Retrofit, OkHttp
* **JSON Parsing:** Kotlinx Serialization
* **Dependency Injection:** Dagger Hilt

## Project Structure

```
app
 ├── data
 │    ├── api             # API services
 │    ├── model           # Data models
 │    └── repository      # Data sources
 │
 ├── di                   # Dependency Injection modules
 │
 ├── navigation           # Compose navigation
 │
 ├── ui                   # Compose screens and components
 │    ├── components
 │    ├── screens
 │    └── theme
 │
 └── util                 # Utility classes
```

## Module Overview

* **Data Module**: Responsible for handling data operations including networking, data models, and repository logic.
* **DI Module**: Manages the dependency injection setup with Dagger Hilt to ensure modularity and ease of testing.
* **Navigation Module**: Contains navigation logic and route definitions for screen transitions.
* **UI Module**: Consists of Jetpack Compose screens and reusable components, following the MVVM pattern.
* **Util Module**: Provides helper classes and extensions used across different modules.

## Setup

Clone this repository and import into Android Studio.

```bash
git clone https://github.com/yourusername/GitHubUserList.git
```

### API Key
GitHub API usage requires a personal access token. 

Generate a token from GitHub settings and place it securely in your `local.properties` file as follows:

 ``` GITHUB_TOKEN="your_github_token_here" ``` 
