package com.kepper104.toiletseverywhere.presentation.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.kepper104.toiletseverywhere.data.Tags
import com.kepper104.toiletseverywhere.data.getDistanceMeters
import com.kepper104.toiletseverywhere.data.getToiletOpenString
import com.kepper104.toiletseverywhere.presentation.MainViewModel
import com.kepper104.toiletseverywhere.presentation.ui.state.CurrentDetailsScreen
import com.ramcosta.composedestinations.annotation.Destination


@Destination
@Composable
fun MapScreen(

) {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
    val mapUiSettings by remember {
        mutableStateOf(MapUiSettings())
    }
    val cameraPositionState = rememberCameraPositionState {
        position = mainViewModel.mapState.cameraPosition
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(mainViewModel.scaffoldPadding),
    ) {

        if (mainViewModel.toiletViewDetailsState.currentDetailScreen == CurrentDetailsScreen.MAP){
            BackHandler (
                onBack = {Log.d("BackLogger", "Handled back from MAP"); mainViewModel.leaveToiletViewDetailsScreen()}
            )
            DetailsScreen()
            return
        }

        if (mainViewModel.newToiletDetailsState.enabled){
            BackHandler (
                onBack = {Log.d("BackLogger", "Handled back from MAP"); mainViewModel.leaveNewToiletDetailsScreen()}
            )
            NewToiletDetailsScreen()
            return
        }

        Log.d(Tags.CompositionLogger.toString(), "Composing map!")


        val newToiletMarkerState = rememberMarkerState(position = LatLng(0.0, 0.0))

        LaunchedEffect(key1 = true){
            mainViewModel.mapState = mainViewModel.mapState.copy(newToiletMarkerState = newToiletMarkerState)
            Log.d(Tags.CompositionLogger.toString(), "Written into mapState: $newToiletMarkerState, now is ${mainViewModel.mapState.newToiletMarkerState}")
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            properties = mainViewModel.mapState.properties,
            uiSettings = mapUiSettings,
            cameraPositionState = cameraPositionState,
            onMapLongClick = {
                if (mainViewModel.mapState.addingToilet){
                    Log.d("ToiletAddLogger", "Adding toilet: $it")
                }
            }
        ) {
            mainViewModel.mapState = mainViewModel.mapState.copy(newToiletMarkerState = MarkerState(position = cameraPositionState.position.target))
            mainViewModel.mapState.newToiletMarkerState?.let {
                Marker(
                    state = it,
                    visible = mainViewModel.mapState.addingToilet
                )
            }

            for(marker in mainViewModel.mapState.toiletMarkers){
                Marker(
                    state = MarkerState(position = marker.position),
                    title = "Public toilet",
                    icon = getToiletIcon(marker.isPublic),
                    snippet = "${getToiletOpenString(marker.toilet)}, ${getDistanceMeters(mainViewModel.mapState.userPosition, marker.position)}",
                    onInfoWindowClick = {mainViewModel.navigateToDetails(marker.toilet, CurrentDetailsScreen.MAP)}
                )
            }
        }
    }
}


enum class ToiletIcons(val icon: BitmapDescriptor){
    ToiletRed(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)),
    ToiletGreen(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
}

fun getToiletIcon(isPublic: Boolean): BitmapDescriptor {
    return if (isPublic){
        ToiletIcons.ToiletGreen.icon
    } else {
        ToiletIcons.ToiletRed.icon
    }
}
fun makeToast(message: String, ctx: Context, length: Int){
    Toast.makeText(ctx, message, length).show()
}
