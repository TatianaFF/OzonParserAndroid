package com.example.ozonpricetracking

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ozonpricetracking.feature.createProduct.presentation.CreateProductDialog
import com.example.ozonpricetracking.feature.details.presentation.DetailsScreen
import com.example.ozonpricetracking.feature.dkma.presentation.DkmaScreen
import com.example.ozonpricetracking.feature.home.presentation.HomeScreen
import com.example.ozonpricetracking.feature.permissions.presentation.PermissionsScreen
import com.example.ozonpricetracking.feature.permissions.presentation.PermissionsScreenViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.serialization.Serializable

@Composable
fun AppNavigation(
    sharedViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val permissionsViewModel: PermissionsScreenViewModel = hiltViewModel()
    val uiState by permissionsViewModel.uiState
    var isPermissionsGranted by remember { mutableStateOf(uiState.allGranted) }

    val isShowCreateDialog by sharedViewModel.isShowCreateDialog.collectAsState()
    val url by sharedViewModel.url.collectAsState()

    LaunchedEffect(uiState.allGranted) {
        if (uiState.allGranted) {
            isPermissionsGranted = true
        }
    }

    LaunchedEffect(currentRoute) {
        if (currentRoute != null) {
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, currentRoute)
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
            }
        }
    }

    if (isShowCreateDialog) {
        CreateProductDialog(
            url = url,
            onDismiss = {
                sharedViewModel.onDismiss()
            }
        )
    }

    if (!isPermissionsGranted) {
        PermissionsScreen(
            viewModel = permissionsViewModel,
            onNavigateToDkma = {
                navController.navigate(DkmaRoute)
            },
            onPermissionsGranted = {
                isPermissionsGranted = true
            },
            onSkip = {
                isPermissionsGranted = true
            }
        )
    } else {
        MainAppContent(
            sharedViewModel = sharedViewModel,
            modifier = modifier
        )
    }
}

@Composable
fun MainAppContent(
    sharedViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    val isShowCreateDialog by sharedViewModel.isShowCreateDialog.collectAsState()
    val url by sharedViewModel.url.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Firebase Analytics
    LaunchedEffect(currentRoute) {
        if (currentRoute != null) {
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, currentRoute)
                param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
            }
        }
    }

    // Диалог создания продукта
    if (isShowCreateDialog) {
        CreateProductDialog(
            url = url,
            onDismiss = {
                sharedViewModel.onDismiss()
            }
        )
    }

    // Навигация
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.92f, animationSpec = tween(200))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.92f, animationSpec = tween(200))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.92f, animationSpec = tween(200))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.92f, animationSpec = tween(200))
        }
    ) {
        composable<HomeRoute> {
//            FirstLaunchChecker(onNavigateToDkma = {
//                navController.navigate(DkmaRoute)
//            })
            HomeScreen(
                onNavigateToProduct = { id ->
                    navController.navigate(DetailsRoute(id))
                },
                onNavigateToCreate = {
                    sharedViewModel.onOpenCreateDialog()
                },
                onNavigateToDkma = {
                    navController.navigate(DkmaRoute)
                }
            )
        }

        composable<DetailsRoute> { backStackEntry ->
            val arguments = backStackEntry.toRoute<DetailsRoute>()

            DetailsScreen(
                id = arguments.id,
                onBack = { navController.popBackStack() },
                onNavigateToDkma = {
                    navController.navigate(DkmaRoute)
                }
            )
        }

        composable<DkmaRoute> {
            DkmaScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Serializable
object HomeRoute

@Serializable
object DkmaRoute

@Serializable
data class DetailsRoute(val id: Long)