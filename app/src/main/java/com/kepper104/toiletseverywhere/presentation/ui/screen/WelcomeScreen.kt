package com.kepper104.toiletseverywhere.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kepper104.toiletseverywhere.data.Tags
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Log.d(Tags.CompositionLogger.toString(), "Composing welcome!")
        Text(
            text = "Welcome to Toilets Here and There!",
        )
        Text(
            text = "Select any of the options below:"
        )
    }
}