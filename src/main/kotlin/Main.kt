package org.example

import MainView
import org.example.model.UserService
import org.example.model.database.DbClient
import org.example.viewmodel.UserViewModel


fun main() {
    val database = DbClient.client.getDatabase("github_db")
    val userSer = UserService(database)
    val viewModel = UserViewModel(userSer)
    MainView(viewModel).start()
}