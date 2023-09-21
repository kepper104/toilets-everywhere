package com.kepper104.toiletseverywhere.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.kepper104.toiletseverywhere.data.Tags
import com.ramcosta.composedestinations.annotation.Destination
import com.google.android.gms.location.*
@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun WelcomeScreen() {
    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    if (locationPermissionState.status.isGranted) {
        Text("Location permission Granted")
    } else {
        Column {
            val textToShow = if (locationPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "Granting user location is required. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "User location permission is required for toilet distance from user to be visible. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
//    val context: Context = LocalContext.current
//
//    val settingResultRequest = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartIntentSenderForResult()
//    ) { activityResult ->
//        if (activityResult.resultCode == RESULT_OK)
//            Log.d("appDebug", "Accepted")
//        else {
//            Log.d("appDebug", "Denied")
//        }
//    }

//    LaunchedEffect(key1 = true){
//        checkLocationSetting(
//            context = context,
//            onDisabled = { intentSenderRequest ->
//                settingResultRequest.launch(intentSenderRequest)
//            },
//            onEnabled = { Log.d(Tags.CompositionLogger.toString(), "Location already enabled") }
//        )
//    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Log.d(Tags.CompositionLogger.toString(), "Composing welcome!")
        Text(
            text = "Welcome to Toilets Everywhere!",
        )
        Text(
            text = "Select any of the options below:"
        )
    }
}

//fun checkLocationSetting(
//    context: Context,
//    onDisabled: (IntentSenderRequest) -> Unit,
//    onEnabled: () -> Unit
//) {
//
//    val locationRequest = LocationRequest.create().apply {
//        interval = 1000
//        fastestInterval = 1000
//        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//    }
//
//    val client: SettingsClient = LocationServices.getSettingsClient(context)
//    val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
//        .Builder()
//        .addLocationRequest(locationRequest)
//
//    val gpsSettingTask: Task<LocationSettingsResponse> =
//        client.checkLocationSettings(builder.build())
//
//    gpsSettingTask.addOnSuccessListener { onEnabled() }
//    gpsSettingTask.addOnFailureListener { exception ->
//        if (exception is ResolvableApiException) {
//            try {
//                val intentSenderRequest = IntentSenderRequest
//                    .Builder(exception.resolution)
//                    .build()
//                onDisabled(intentSenderRequest)
//            } catch (sendEx: IntentSender.SendIntentException) {
//                // ignore here
//            }
//        }
//    }
//}