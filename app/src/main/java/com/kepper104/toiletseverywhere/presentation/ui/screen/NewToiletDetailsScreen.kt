package com.kepper104.toiletseverywhere.presentation.ui.screen

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.kepper104.toiletseverywhere.presentation.MainViewModel

@Composable
fun NewToiletDetailsScreen() {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)

    Text(text = "Adding toilet at ${mainViewModel.newToiletDetailsState.coordinates}")

}