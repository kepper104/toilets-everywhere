package com.kepper104.toiletseverywhere.presentation.ui.screen

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kepper104.toiletseverywhere.data.Tags
import com.kepper104.toiletseverywhere.presentation.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination

@Preview
@Destination
@Composable
fun DetailsScreen() {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
    Log.d(Tags.CompositionLogger.toString(), "Composing details screen")
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
    ){
        val toiletInfo = mainViewModel.detailsState
        Text(text =
        if (toiletInfo.toilet!!.isPublic)
        {"Public Toilet"}
        else
        { "Toilet in ${toiletInfo.toilet.placeName}" })
        Text(text = "Created by ${toiletInfo.authorName} ${toiletInfo.toilet.creationDate.toString()}")

    }

}