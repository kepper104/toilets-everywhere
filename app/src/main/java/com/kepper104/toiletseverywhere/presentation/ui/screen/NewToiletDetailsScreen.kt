package com.kepper104.toiletseverywhere.presentation.ui.screen


import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kepper104.toiletseverywhere.presentation.MainViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewToiletDetailsScreen() {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)


    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(mainViewModel.scaffoldPadding)

    ) {
        Text(text = "Creating a new toilet", fontSize = 40.sp)

        Text(text = "Please fill in some details about the new toilet:")
        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(text = "Is it public?")

            Checkbox(
                checked = mainViewModel.newToiletDetailsState.isPublic,
                onCheckedChange = {
                    mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(isPublic = it)
                })
        }

        if (!mainViewModel.newToiletDetailsState.isPublic){
            Text(text = "Name a place in which this toilet is in")
            Text(text = "Example: Cofix")
            TextField(
                value = mainViewModel.newToiletDetailsState.name,
                onValueChange = {mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(name = it)},
            )
        }

        
        Text(text = "Click on conveniences available at this toilet")

        Row {
            Checkbox(
                checked = mainViewModel.newToiletDetailsState.disabledAccess,
                onCheckedChange = {mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(disabledAccess = it)})
            Text(text = "Wheelchair Accessible")
        }
        Row {
            Checkbox(
                checked = mainViewModel.newToiletDetailsState.babyAccess,
                onCheckedChange = {mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(babyAccess = it)})
            Text(text = "Has baby changing station")
        }
        Row {
            Checkbox(
                checked = mainViewModel.newToiletDetailsState.parkingNearby,
                onCheckedChange = {mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(parkingNearby = it)})
            Text(text = "Has parking nearby")
        }

        Row {
            Text(text = "Visit price, leave 0 if free")
            TextField(
                value = mainViewModel.newToiletDetailsState.cost.toString(),
                onValueChange = {mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(cost = it.toInt())}
            )
        }

        Text(text = "Working hours:")

//        Button(onClick = { timePickerOpen = true }) {
//            Text(text = "Pick opening time")
//        }

        TimePickers(mainViewModel)

//            Spacer(modifier = Modifier.width(10.dp))



    }

}

@Composable
fun TimePickers(vm: MainViewModel) {



    val formattedOpeningTime by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("hh:mm")
                .format(vm.newToiletDetailsState.openingTime)
        }
    }

    val timeDialogState = rememberMaterialDialogState()

    val context = LocalContext.current

    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = {
            timeDialogState.show()
        }) {
            Text(text = "Pick time $id")
        }
        Text(text = formattedOpeningTime)
    }

    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                Toast.makeText(
                    context,
                    "Clicked ok",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(text = "Cancel")
        }
    ) {
        timepicker(
            initialTime = LocalTime.NOON,
            title = "Pick a time",
            timeRange = LocalTime.MIDNIGHT..LocalTime.MAX
        ) {
            pickedTime = it
        }
    }
}
