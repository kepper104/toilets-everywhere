package com.kepper104.toiletseverywhere.presentation.navigation

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kepper104.toiletseverywhere.data.BottomBarDestination
import com.kepper104.toiletseverywhere.data.NOT_LOGGED_IN_STRING
import com.kepper104.toiletseverywhere.data.ScreenEvent
import com.kepper104.toiletseverywhere.data.Tags
import com.kepper104.toiletseverywhere.presentation.MainViewModel
import com.kepper104.toiletseverywhere.presentation.ui.screen.NavGraphs
import com.kepper104.toiletseverywhere.presentation.ui.screen.appCurrentDestinationAsState
import com.kepper104.toiletseverywhere.presentation.ui.screen.destinations.DetailsScreenDestination
import com.kepper104.toiletseverywhere.presentation.ui.screen.destinations.MapScreenDestination
import com.kepper104.toiletseverywhere.presentation.ui.screen.destinations.TypedDestination
import com.kepper104.toiletseverywhere.presentation.ui.screen.startAppDestination
import com.kepper104.toiletseverywhere.presentation.ui.state.CurrentDetailsScreen
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popBackStack
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.utils.isRouteOnBackStack
import kotlinx.coroutines.flow.collectLatest


val destinationToDetailScreenMapping = mapOf(CurrentDetailsScreen.MAP to BottomBarDestination.MapView, CurrentDetailsScreen.LIST to BottomBarDestination.ListView)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
) {
    val currentDestination: TypedDestination<out Any?> = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)


    NavigationBar {
        BottomBarDestination.values().forEach { destination ->
            val isSelected = currentDestination == destination.direction
            val isCurrentDestOnBackStack = navController.isRouteOnBackStack(destination.direction)
            NavigationBarItem(
                selected = isSelected,

                onClick = {
                    if (mainViewModel.navigationState.currentDestination == destination){
                        mainViewModel.leaveDetailsScreen()
                    }
                    mainViewModel.changeNavigationState(destination)

                    if (isCurrentDestOnBackStack){
                        navController.popBackStack(destination.direction, false)
                        return@NavigationBarItem
                    }
                    navController.navigate(destination.direction){
                        popUpTo(NavGraphs.root){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    if (isSelected)
                        Icon(destination.iconSelected, contentDescription = "icon")
                    else
                    {
                        Icon(destination.iconUnselected, contentDescription = "icon")
                    }},

                label = { Text(text = destination.label) }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavScaffold(
    navController: NavHostController,
    bottomBar: @Composable (TypedDestination<out Any?>) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val destination =
        navController.appCurrentDestinationAsState().value
        ?:
        MapScreenDestination


    Scaffold(
        topBar = { MapTopAppBar()},
        bottomBar = { bottomBar(destination) },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTopAppBar() {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)

    TopAppBar(
        title = {
            when (mainViewModel.navigationState.currentDestination) {
                null -> {
                    Text(text = "Welcome!")
                }
                BottomBarDestination.MapView -> {

                    Text(text = "Toilet Map")
                }
                BottomBarDestination.ListView -> {
                    Text(text = "Toilet List")

                }
                BottomBarDestination.Settings -> {
                    Text(text = "Settings")
                }
            }
        },

        navigationIcon = {
            if(destinationToDetailScreenMapping[mainViewModel.detailsState.currentDetailScreen] == mainViewModel.navigationState.currentDestination && mainViewModel.navigationState.currentDestination != null){
                IconButton(onClick = { mainViewModel.leaveDetailsScreen() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                }
            }

        },
        actions = {
            if (mainViewModel.detailsState.currentDetailScreen != CurrentDetailsScreen.NONE && destinationToDetailScreenMapping[mainViewModel.detailsState.currentDetailScreen] == mainViewModel.navigationState.currentDestination){
                return@TopAppBar
            }
            if (mainViewModel.navigationState.currentDestination == BottomBarDestination.MapView){
                Log.d(Tags.CompositionLogger.toString(), "Showing buttons for MapView")

                if (mainViewModel.loggedInUserState.currentUserName != NOT_LOGGED_IN_STRING){
                    Log.d(Tags.CompositionLogger.toString(), "Showing Add Toilet button")
                    IconButton(onClick = {
                        if (!mainViewModel.mapState.addingToilet){
                            mainViewModel.triggerEvent(ScreenEvent.ToiletAddingEnabledToast)
                            mainViewModel.mapState = mainViewModel.mapState.copy(addingToilet = true)
                        }else{
                            mainViewModel.triggerEvent(ScreenEvent.ToiletAddingDisabledToast)
                            mainViewModel.mapState = mainViewModel.mapState.copy(addingToilet = false)
                        }

                    }) {
                        Icon(imageVector =
                                if (mainViewModel.mapState.addingToilet)
                                    Icons.Filled.AddCircle
                                else
                                    Icons.Filled.AddCircleOutline,

                            contentDescription = "Add a new toilet")
                    }
                }
                else{
                    Log.d(Tags.CompositionLogger.toString(), "NOT Showing Add Toilet button")

                }
                IconButton(onClick = { mainViewModel.placeholder() }) {
                    Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Filter toilets")
                }
                IconButton(onClick = { mainViewModel.getLatestToilets() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh toilets")
                }

            }else if (mainViewModel.navigationState.currentDestination == BottomBarDestination.ListView){
                IconButton(onClick = { mainViewModel.placeholder() }) {
                    Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Filter toilets")
                }
                IconButton(onClick = { mainViewModel.getLatestToilets() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh toilets")
                }
            }
        }
    )
}
