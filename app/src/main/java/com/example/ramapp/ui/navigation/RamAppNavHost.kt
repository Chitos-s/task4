package com.example.ramapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ramapp.ui.screens.CharacterDetailScreen
import com.example.ramapp.ui.screens.CharacterGridScreen
import com.example.ramapp.ui.viewmodel.CharacterViewModel

@Composable
fun RamAppNavHost() {
    val navController = rememberNavController()
    val viewModel: CharacterViewModel = hiltViewModel()
    val listState = viewModel.listState
    val detailState = viewModel.detailState
    val selectedFilters = viewModel.selectedFilters

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CharacterGridScreen(
                listState = listState,
                selectedFilters = selectedFilters,
                onRetry = { viewModel.retryCurrentPage() },
                onCharacterClick = { id -> navController.navigate("detail/$id") },
                onApplyFilters = { filters -> viewModel.applyFilters(filters) },
                onNextPage = { viewModel.nextPage() },
                onPrevPage = { viewModel.prevPage() }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            LaunchedEffect(id) {
                viewModel.loadCharacter(id)
            }

            CharacterDetailScreen(
                detailState = detailState,
                onRetry = { viewModel.loadCharacter(id) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
