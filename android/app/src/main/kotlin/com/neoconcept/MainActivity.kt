package com.neoconcept

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neoconcept.ui.bookdetail.BookDetailScreen
import com.neoconcept.ui.home.HomeScreen
import com.neoconcept.ui.intro.IntroScreen
import com.neoconcept.ui.theme.NeoConceptTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NeoConceptTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onBookClick = { bookId ->
                                navController.navigate("book/$bookId")
                            },
                        )
                    }
                    composable(
                        route = "book/{bookId}",
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
                    ) { backStackEntry ->
                        val bookId = checkNotNull(backStackEntry.arguments?.getString("bookId"))
                        BookDetailScreen(
                            onBackClick = { navController.popBackStack() },
                            onLessonClick = { refPath, lessonId ->
                                val encodedPath = Uri.encode(refPath)
                                navController.navigate("intro/$bookId/$encodedPath/$lessonId")
                            },
                        )
                    }
                    composable(
                        route = "intro/{bookId}/{refPath}/{lessonId}",
                        arguments =
                            listOf(
                                navArgument("bookId") { type = NavType.StringType },
                                navArgument("refPath") { type = NavType.StringType },
                                navArgument("lessonId") { type = NavType.StringType },
                            ),
                    ) {
                        IntroScreen(
                            onBackClick = { navController.popBackStack() },
                            onStartLearning = {
                                navController.popBackStack()
                            },
                        )
                    }
                }
            }
        }
    }
}
