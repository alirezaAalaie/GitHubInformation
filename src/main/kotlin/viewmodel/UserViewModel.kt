package org.example.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.model.UserService
import org.example.model.database.UserRepoEntity
import org.example.model.github.model.RepositoryDto
import retrofit2.HttpException

class UserViewModel(private val userService: UserService) {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private val _users = MutableStateFlow<List<UserRepoEntity>>(emptyList())
    val users: StateFlow<List<UserRepoEntity>> get() = _users

    private val _repos = MutableStateFlow<List<RepositoryDto>>(emptyList())
    val repos: StateFlow<List<RepositoryDto>> get() = _repos


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchUser(username: String) {
        viewModelScope.launch {
            _users.value = emptyList()
            _isLoading.value = true
            try {
                val user = userService.getUser(username)
                _users.value = listOf(user)
                _error.value = null
            } catch (ex: HttpException) {
                _error.value = if (ex.code() == 404) "Failed to fetch user: username not found" else throw ex
            } catch (e: Exception) {
                _error.value = "Failed to fetch user: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _users.value = emptyList()
            _isLoading.value = true
            try {
                _users.value = userService.getAllUsers()
                if (_users.value.isEmpty()) {
                    _error.value = "No users found"
                } else {
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Failed to fetch users: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchUser(username: String) {
        viewModelScope.launch {
            _users.value = emptyList()
            _isLoading.value = true
            try {
                val user = userService.searchUserByUsername(username)
                user?.let {
                    _users.value = listOf(it)
                    _error.value = null
                } ?: run {
                    _error.value = "User not found"
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchRepository(repoName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _repos.value = emptyList()
            try {
                val repo = userService.searchRepositoryByName(repoName)
                repo.let {
                    _repos.value = it
                    _error.value = null
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}