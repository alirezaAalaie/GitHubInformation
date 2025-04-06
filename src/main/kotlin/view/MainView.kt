import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.example.model.database.UserRepoEntity
import org.example.viewmodel.UserViewModel
import java.util.*

class MainView(private val viewModel: UserViewModel) {
    private val scanner = Scanner(System.`in`)
    private val scope = CoroutineScope(Dispatchers.Default)
    private var currentCollectJob: Job? = null

    fun start() {
        scope.launch {
            viewModel.error.collect { error ->
                error?.let {
                    println("\u001B[31mError: $it\u001B[0m")
                }
            }
        }

        scope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    print("\u001B[33mLoading...\u001B[0m")
                }
            }
        }

        while (true) {
            printMenu()
            when (scanner.nextLine().trim()) {
                "1" -> fetchUser()
                "2" -> showAllUsers()
                "3" -> searchUser()
                "4" -> searchRepositories()
                "5" -> {
                    println("Exiting...")
                    scope.cancel()
                    return
                }

                else -> println("Invalid option")
            }
        }
    }

    private fun printMenu() {
        println("\n=== GitHub User Explorer ===")
        println("1. Get user by username")
        println("2. Show all cached users")
        println("3. Search users in cache")
        println("4. Search repositories in cache")
        println("5. Exit")
        print("Select an option: ")
    }

    private fun fetchUser() {
        currentCollectJob?.cancel()
        viewModel.clearState()

        print("Enter GitHub username: ")
        val username = scanner.nextLine().trim()
        if (username.isNotEmpty()) {
            viewModel.fetchUser(username)
            currentCollectJob = scope.launch {
                viewModel.users.collect { users ->
                    users.lastOrNull()?.let {
                        println("\n\u001B[32mUser fetched successfully:\u001B[0m")
                        printUserDetails(it)
                    }
                }
            }
        } else {
            println("\u001B[31mError: Username is empty")
        }
    }

    private fun showAllUsers() {
        currentCollectJob?.cancel()
        viewModel.clearState()
        viewModel.fetchAllUsers()
        currentCollectJob = scope.launch {
            viewModel.users.collect { users ->
                if (users.isNotEmpty()) {
                    println("\n\u001B[34mCached Users (${users.size}):\u001B[0m")
                    users.forEachIndexed { index, user ->
                        println("${index + 1}. ${user.login} (${user.publicRepos.toInt()} repos)")
                    }
                }
            }
        }
    }

    private fun searchUser() {
        currentCollectJob?.cancel()
        viewModel.clearState()

        print("Enter username to search: ")
        val query = scanner.nextLine().trim()
        if (query.isNotEmpty()) {
            viewModel.searchUser(query)
            currentCollectJob = scope.launch {
                viewModel.users.collect { users ->
                    if (users.isNotEmpty()) {
                        println("\nSearch Results:")
                        users.forEach { user ->
                            printUserDetails(user)
                        }
                    }
                }
            }
        } else {
            println("\u001B[31mError: Your query is empty")
        }
    }

    private fun searchRepositories() {
        currentCollectJob?.cancel()
        viewModel.clearState()

        print("Enter repository name to search: ")
        val query = scanner.nextLine().trim()
        if (query.isNotEmpty()) {
            viewModel.searchRepository(query)
            currentCollectJob = scope.launch {
                viewModel.repos.collect { repos ->
                    if (repos.isNotEmpty()) {
                        println("\nFound ${repos.size} repositories:")
                        repos.forEachIndexed { index, repo ->
                            println("\u001B[36m${index + 1}. ${repo.fullName}\u001B[0m")
                            println("Owner: ${repo.owner}")
                            println("  description:  ${repo.description ?: "No description"}")
                            println("  URL: ${repo.url}\n")
                        }
                        println("────────────────────")
                    }
                }
            }
        } else {
            println("\u001B[31mError: Your query is empty")
        }
    }

    private fun printUserDetails(user: UserRepoEntity) {
        println("\u001B[1m${user.login}\u001B[0m")
        println("Name: ${user.name ?: "Not specified"}")
        println("Followers: ${user.followers.toInt()} | Following: ${user.following.toInt()}")
        println("Public repos: ${user.publicRepos.toInt()}")
        println("Created at: ${user.createdAt}")
        user.repositories.forEachIndexed { index, repo ->
            println("\u001B[36m${index + 1}. ${repo.fullName}\u001B[0m")
            println("  description:  ${repo.description ?: "No description"}")
            println("  URL: ${repo.url}\n")
        }
        println("────────────────────")
    }
}