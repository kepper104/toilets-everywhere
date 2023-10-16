package com.kepper104.toiletseverywhere.presentation.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kepper104.toiletseverywhere.presentation.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewToiletDetailsScreen() {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(mainViewModel.scaffoldPadding)

    ) {
        Text(text = "Creating a new toilet")
        Text(text = "Please fill in some details about the new toilet:")
        Text(text = "Is it public?")

        Checkbox(
            checked = mainViewModel.newToiletDetailsState.isPublic,
            onCheckedChange = {
                    mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(isPublic = it)
        })

        TextField(
            value = mainViewModel.newToiletDetailsState.name,
            onValueChange = {mainViewModel.newToiletDetailsState = mainViewModel.newToiletDetailsState.copy(name = it)},
            readOnly = !mainViewModel.newToiletDetailsState.isPublic
        )

    }

}