package com.neoconcept.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neoconcept.data.repository.ManifestRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.screens.CourseListScreen
import com.neoconcept.screens.LessonScreen
import com.neoconcept.screens.SettingsScreen
import com.neoconcept.screens.StatsScreen
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object CourseList : Screen("course_list", "课程", Icons.AutoMirrored.Filled.List)
    data object Stats : Screen("stats", "统计", Icons.Default.Star)
    data object Settings : Screen("settings", "设置", Icons.Default.Settings)
}

val screens = listOf(Screen.CourseList, Screen.Stats, Screen.Settings)

@Composable
fun AppNavigation(
    manifestRepository: ManifestRepository,
    progressRepository: ProgressRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in screens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                SwissTabBar(
                    screens = screens,
                    currentRoute = currentRoute,
                    onTabSelected = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.CourseList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.CourseList.route) {
                CourseListScreen(
                    manifestRepository = manifestRepository,
                    onLessonClick = { lessonId: String ->
                        navController.navigate("lesson/$lessonId")
                    }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
            composable(
                route = "lesson/{lessonId}",
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                LessonScreen(
                    lessonId = lessonId,
                    manifestRepository = manifestRepository,
                    progressRepository = progressRepository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun SwissTabBar(
    screens: List<Screen>,
    currentRoute: String?,
    onTabSelected: (Screen) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(SwissBorder.widthThick)
                .background(SwissColor.border)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(SwissColor.bg),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val isSelected = currentRoute == screen.route
                SwissTabItem(
                    screen = screen,
                    isSelected = isSelected,
                    onClick = { onTabSelected(screen) }
                )
            }
        }
    }
}

@Composable
private fun SwissTabItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val opacity = if (isSelected) 1f else 0.4f

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = screen.icon,
            contentDescription = screen.title,
            modifier = Modifier.size(22.dp),
            tint = SwissColor.fg.copy(alpha = opacity)
        )
        Text(
            text = screen.title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = SwissColor.fg.copy(alpha = opacity),
            textAlign = TextAlign.Center
        )
    }
}
