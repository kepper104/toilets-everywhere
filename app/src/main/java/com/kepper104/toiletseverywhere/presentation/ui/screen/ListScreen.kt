package com.kepper104.toiletseverywhere.presentation.ui.screen

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kepper104.toiletseverywhere.data.Tags
import com.kepper104.toiletseverywhere.domain.model.Toilet
import com.kepper104.toiletseverywhere.presentation.MainViewModel
import com.kepper104.toiletseverywhere.presentation.ui.state.CurrentDetailsScreen
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ListScreen(

) {
    val mainViewModel: MainViewModel = hiltViewModel(LocalContext.current as ComponentActivity)



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(mainViewModel.scaffoldPadding),
    ) {

        if (mainViewModel.detailsState.currentDetailScreen == CurrentDetailsScreen.LIST){
            BackHandler (
                onBack = {Log.d("BackLogger", "Handled back from MAP");mainViewModel.leaveDetailsScreen()}
            )
            DetailsScreen()
            return
        }

        Log.d(Tags.CompositionLogger.toString(), "Composing list!")
//        Text(text = "List Screen")
        
        LazyColumn(

        ){
            for (toilet in mainViewModel.toiletsState.toiletList){
                item{
                    ToiletCard(toilet = toilet, navigateToDetails = mainViewModel::navigateToDetails)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
@Preview
@Composable
fun ToiletCard(toilet: Toilet = Toilet(), navigateToDetails: (toilet: Toilet, source: CurrentDetailsScreen) -> Unit = ::placeHolderFunc ) {
    Column(
        modifier = Modifier
            .clickable {
                navigateToDetails(toilet, CurrentDetailsScreen.LIST)
            }
    ) {
        Row {
//            Text(text = "ID: ${toilet.id}   ")
            Text(
                text = if (toilet.isPublic) "Public toilet" else {"a"}
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(text = "Created by ${toilet.authorId} on ${toilet.creationDate}")
        }
        Row {

        }
    }

}

fun placeHolderFunc(toilet: Toilet, source: CurrentDetailsScreen): Unit {

}

@Composable
fun AttributeBadge() {
    
}