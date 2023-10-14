package com.kepper104.toiletseverywhere.presentation.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kepper104.toiletseverywhere.data.Tags
import com.kepper104.toiletseverywhere.presentation.MainViewModel
import com.kepper104.toiletseverywhere.presentation.navigation.BottomNavigationBar
import com.kepper104.toiletseverywhere.presentation.navigation.NavScaffold
import com.kepper104.toiletseverywhere.presentation.ui.screen.destinations.AuthScreenDestination
import com.kepper104.toiletseverywhere.presentation.ui.screen.destinations.MainScreenDestination
import com.kepper104.toiletseverywhere.presentation.ui.screen.destinations.WelcomeScreenDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Route


@RootNavGraph(start = true)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun MainScreen(

) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
    var currentRoute: Route = AuthScreenDestination

    val isLoggedInFlowChecker = mainViewModel.isLoggedInFlow.collectAsState(initial = null)
    mainViewModel.addLoggedInChecker(isLoggedInFlowChecker)

    if (isLoggedInFlowChecker.value == false){
        currentRoute = AuthScreenDestination
        Log.d(Tags.CompositionLogger.toString(), "Going to login")

        navController.navigate(AuthScreenDestination){
            launchSingleTop = true
        }
    } else if (isLoggedInFlowChecker.value == true){
        currentRoute = WelcomeScreenDestination
        Log.d(Tags.CompositionLogger.toString(), "Going to welcome")

        navController.navigate(MainScreenDestination){
            launchSingleTop = true
        }
    }

    NavScaffold(
        navController = navController,
        bottomBar = {
            if (it != AuthScreenDestination){
                BottomNavigationBar(
                    navController = navController,
                )
            }
        }) {

        LaunchedEffect(key1 = true){
            mainViewModel.setPadding(it)
        }

        DestinationsNavHost(
            navGraph = NavGraphs.root,
            navController = navController,
            startRoute = currentRoute
        )
    }
}
