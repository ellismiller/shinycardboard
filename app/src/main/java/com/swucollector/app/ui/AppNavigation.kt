package com.swucollector.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swucollector.app.ui.collection.CollectionScreen
import com.swucollector.app.ui.deck.DeckDetailScreen
import com.swucollector.app.ui.deck.DeckListScreen
import com.swucollector.app.ui.playset.PlaysetScreen
import com.swucollector.app.ui.setcompletion.SetCompletionScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Collection : Screen("collection", "Collection", Icons.Default.CollectionsBookmark)
    object Playsets : Screen("playsets", "Playsets", Icons.Default.AutoAwesome)
    object Sets : Screen("sets", "Sets", Icons.Default.GridView)
    object Decks : Screen("decks", "Decks", Icons.Default.Dashboard)
}

private val bottomNavScreens = listOf(
    Screen.Collection,
    Screen.Playsets,
    Screen.Sets,
    Screen.Decks
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = bottomNavScreens.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Collection.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Collection.route) { CollectionScreen() }
            composable(Screen.Playsets.route) { PlaysetScreen() }
            composable(Screen.Sets.route) { SetCompletionScreen() }
            composable(Screen.Decks.route) {
                DeckListScreen(onDeckClick = { deckId ->
                    navController.navigate("deck/$deckId")
                })
            }
            composable(
                route = "deck/{deckId}",
                arguments = listOf(navArgument("deckId") { type = NavType.LongType })
            ) { backStack ->
                val deckId = backStack.arguments!!.getLong("deckId")
                DeckDetailScreen(
                    deckId = deckId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
