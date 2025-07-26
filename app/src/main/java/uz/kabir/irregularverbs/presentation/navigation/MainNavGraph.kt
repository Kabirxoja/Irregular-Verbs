package uz.kabir.irregularverbs.presentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import uz.kabir.irregularverbs.R
import uz.kabir.irregularverbs.presentation.ui.components.BottomNavigationBar
import uz.kabir.irregularverbs.presentation.ui.screens.home.HomeFragment
import uz.kabir.irregularverbs.presentation.ui.screens.learn.LearnFragment
import uz.kabir.irregularverbs.presentation.ui.screens.listen.ListenFragment
import uz.kabir.irregularverbs.presentation.ui.screens.option.OptionFragment
import uz.kabir.irregularverbs.presentation.ui.screens.search.SearchFragment
import uz.kabir.irregularverbs.presentation.ui.screens.setting.SettingsFragment
import uz.kabir.irregularverbs.presentation.ui.components.BottomNavItem
import uz.kabir.irregularverbs.presentation.ui.screens.listenresult.ListenResultFragment
import uz.kabir.irregularverbs.presentation.ui.screens.optionresult.OptionResultFragment
import uz.kabir.irregularverbs.presentation.ui.screens.write.WriteFragment
import uz.kabir.irregularverbs.presentation.ui.screens.writeresult.WriteResultFragment
import uz.kabir.irregularverbs.presentation.ui.screens.listen.ListenViewModel
import uz.kabir.irregularverbs.presentation.ui.screens.option.OptionViewModel
import uz.kabir.irregularverbs.presentation.ui.screens.write.WriteViewModel

import kotlin.collections.contains

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.Home.route
    ) {
        composable(Screens.Home.route) {
            MainScreen()
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val innerNavController = rememberNavController()

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 580

    val items = listOf(
        BottomNavItem(Screens.Home.route, stringResource(R.string.home), R.drawable.ic_home),
        BottomNavItem(Screens.Learn.route, stringResource(R.string.learn), R.drawable.ic_book),
        BottomNavItem(Screens.Search.route, stringResource(R.string.search), R.drawable.ic_search),
        BottomNavItem(Screens.Settings.route, stringResource(R.string.settings), R.drawable.ic_settings),
    )

    val currentBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (!isTablet && showBottomBar) {
                BottomNavigationBar(navController = innerNavController, items = items)
            }
        }
    ) { padding ->
        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (showBottomBar) {
                    Column(
                        modifier = Modifier
                            .width(72.dp)
                            .fillMaxHeight()
                    ) {
                        BottomNavigationBar(navController = innerNavController, items = items)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    MainInnerNavHost(navController = innerNavController)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                MainInnerNavHost(navController = innerNavController)
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainInnerNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screens.Home.route) { HomeFragment(navController) }
        composable(Screens.Learn.route) { LearnFragment(navController) }
        composable(Screens.Search.route) { SearchFragment(navController) }
        composable(Screens.Settings.route) { SettingsFragment(navController) }

        navigation(
            startDestination = Screens.Option.route,
            route = Screens.QuizGraph.routeWithArg,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType }) //argumentni olish
        ) {
            composable(
                route = Screens.Option.route,
                arguments = listOf(navArgument("groupId") {
                    type = NavType.IntType
                }) //argumentni olish
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.QuizGraph.routeWithArg) //parent graph ni olish
                }
                val viewModel: OptionViewModel = hiltViewModel(parentEntry)
                OptionFragment(navController = navController, optionViewModel = viewModel)
            }

            composable(
                route = Screens.OptionResult.routeWithArg,
                arguments = listOf(navArgument("groupId") {
                    type = NavType.IntType
                }) //argumentni olish
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.QuizGraph.routeWithArg) //parent graph ni olish
                }
                val viewModel: OptionViewModel = hiltViewModel(parentEntry)
                OptionResultFragment(navController = navController, optionViewModel = viewModel)
            }
        }

        navigation(
            startDestination = Screens.Listen.route,
            route = Screens.ListenGraph.routeWithArg,
            arguments = listOf(navArgument("groupId") {
                type = NavType.IntType
            })
        ) {
            composable(
                route = Screens.Listen.route,
                arguments = listOf(navArgument("groupId") { type = NavType.IntType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.ListenGraph.routeWithArg) // ListGraph.routeWithArg
                }
                val viewModel: ListenViewModel = hiltViewModel(parentEntry)
                ListenFragment(navController = navController, listenViewModel = viewModel)
            }
            composable(
                route = Screens.ListenResult.routeWithArg,
                arguments = listOf(navArgument("groupId") {
                    type = NavType.IntType
                }) // SavedStateHandle
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Screens.ListenGraph.routeWithArg) // ListGraph.routeWithArg
                }
                val viewModel: ListenViewModel = hiltViewModel(parentEntry)
                ListenResultFragment(navController = navController, listenViewModel = viewModel)
            }

        }

        navigation(
            startDestination = Screens.Write.route,
            route = Screens.WriteGraph.routeWithArg,
            arguments = listOf(navArgument("groupId") {
                type = NavType.IntType
            })
        ) {
            composable(route = Screens.Write.route, arguments = listOf(navArgument("groupId") { type = NavType.IntType })) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screens.WriteGraph.routeWithArg) }
                val viewmodel: WriteViewModel = hiltViewModel(parentEntry)
                WriteFragment(navController = navController, writeViewmodel = viewmodel)
            }

            composable(route = Screens.WriteResult.routeWithArg, arguments = listOf(navArgument("groupId") { type = NavType.IntType })) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screens.WriteGraph.routeWithArg) }
                val viewModel: WriteViewModel = hiltViewModel(parentEntry)
                WriteResultFragment(navController = navController, writeViewModel = viewModel)
            }
        }


    }
}