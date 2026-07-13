package com.neoconcept

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neoconcept.ui.bookdetail.BookDetailScreen
import com.neoconcept.ui.completion.CompletionScreen
import com.neoconcept.ui.home.HomeScreen
import com.neoconcept.ui.intro.IntroScreen
import com.neoconcept.ui.lesson.LessonScreen
import com.neoconcept.ui.theme.NeoConceptTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NeoConceptTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
private fun AppNavigation() {
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
        ) { backStackEntry ->
            val bookId = checkNotNull(backStackEntry.arguments?.getString("bookId"))
            val refPath = checkNotNull(backStackEntry.arguments?.getString("refPath"))
            val lessonId = checkNotNull(backStackEntry.arguments?.getString("lessonId"))
            IntroScreen(
                onBackClick = { navController.popBackStack() },
                onStartLearning = {
                    val encodedRefPath = Uri.encode(refPath)
                    navController.navigate("lesson/$bookId/$encodedRefPath/$lessonId/1")
                },
            )
        }
        lessonRoute(navController = navController)
        completionRoute(navController = navController)
    }
}

private fun NavGraphBuilder.lessonRoute(navController: NavController) {
    composable(
        route = "lesson/{bookId}/{refPath}/{lessonId}/{step}",
        arguments =
            listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("refPath") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType },
                navArgument("step") { type = NavType.IntType },
            ),
    ) { backStackEntry ->
        val bookId = checkNotNull(backStackEntry.arguments?.getString("bookId"))
        val lessonId = checkNotNull(backStackEntry.arguments?.getString("lessonId"))
        LessonScreen(
            onBackClick = {
                navController.popBackStack("book/$bookId", inclusive = false)
            },
            onComplete = {
                navController.navigate("completion/$bookId/$lessonId")
            },
        )
    }
}

private fun NavGraphBuilder.completionRoute(navController: NavController) {
    composable(
        route = "completion/{bookId}/{lessonId}",
        arguments =
            listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("lessonId") { type = NavType.StringType },
            ),
    ) { backStackEntry ->
        val bookId = checkNotNull(backStackEntry.arguments?.getString("bookId"))
        CompletionScreen(
            onBackToBook = {
                navController.popBackStack("book/$bookId", inclusive = false)
            },
            onNextLesson = { nextLesson ->
                val encodedPath = Uri.encode(nextLesson.path)
                navController.navigate("intro/$bookId/$encodedPath/${nextLesson.id}") {
                    popUpTo("book/$bookId") { inclusive = false }
                }
            },
        )
    }
}
