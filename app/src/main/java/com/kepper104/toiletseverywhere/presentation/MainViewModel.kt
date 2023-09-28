package com.kepper104.toiletseverywhere.presentation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.kepper104.toiletseverywhere.data.AuthUiStatus
import com.kepper104.toiletseverywhere.data.BottomBarDestination
import com.kepper104.toiletseverywhere.data.LoginStatus
import com.kepper104.toiletseverywhere.data.RegistrationError
import com.kepper104.toiletseverywhere.data.Tags
import com.kepper104.toiletseverywhere.data.toToiletMarker
import com.kepper104.toiletseverywhere.domain.model.Toilet
import com.kepper104.toiletseverywhere.domain.repository.Repository
import com.kepper104.toiletseverywhere.presentation.ui.state.AuthState
import com.kepper104.toiletseverywhere.presentation.ui.state.CurrentDetailsScreen
import com.kepper104.toiletseverywhere.presentation.ui.state.DetailsState
import com.kepper104.toiletseverywhere.presentation.ui.state.MapState
import com.kepper104.toiletseverywhere.presentation.ui.state.NavigationState
import com.kepper104.toiletseverywhere.presentation.ui.state.ToiletsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject


const val MIN_PASSWORD_LENGTH = 8


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var toiletsState by mutableStateOf(ToiletsState())
    var mapState by mutableStateOf(MapState())
    var authState by mutableStateOf(AuthState())
    var detailsState by mutableStateOf(DetailsState())
    var navigationState by mutableStateOf(NavigationState())


    lateinit var scaffoldPadding: PaddingValues

    init {
        collectLoginStatusFlow()
        try {
            getLatestToilets()
        } catch (e: Exception){
            Log.e(Tags.MainViewModelTag.toString(), "Error connecting to server")
        }
    }

    private var prevLoggedInValue = false

    var isLoggedInFlow = flow {
        emit(null)

        while(true){
            if (prevLoggedInValue != repository.currentUser.isLoggedIn){
                Log.d(Tags.MainViewModelTag.toString(), "Status changed! $prevLoggedInValue -> ${repository.currentUser.isLoggedIn}")
                emit(repository.currentUser.isLoggedIn)
                prevLoggedInValue = repository.currentUser.isLoggedIn
            }
            delay(200L)
        }
    }



    private var prevLoginStatus: LoginStatus = LoginStatus.None

    private var loginStatusFlow = flow {
        emit(LoginStatus.None)

        while(true){
            if (prevLoginStatus != repository.loginStatus){
                Log.d(Tags.MainViewModelTag.toString(), "Status changed! $prevLoginStatus -> ${repository.loginStatus}")
                emit(repository.loginStatus)
                prevLoginStatus = repository.loginStatus
            }
            delay(200L)
        }
    }

    fun enableLocationServices(locationClient: FusedLocationProviderClient){
        mapState = mapState.copy(properties = MapProperties(isMyLocationEnabled = true))
        viewModelScope.launch {
            try{
                Log.d(Tags.MainViewModelTag.toString(), "Getting position on LocationServices enable")
                val res = locationClient.lastLocation
                res.addOnSuccessListener {
                    mapState = mapState.copy(cameraPosition = CameraPosition(LatLng(res.result.latitude, res.result.longitude), 15F, 0F, 0F))
                    Log.d(Tags.MainViewModelTag.toString(), "Successfully got position on LocationServices enable")
                }
                res.addOnFailureListener{
                    Log.e(Tags.MainViewModelTag.toString(),  it.toString())
                }



            } catch (e: SecurityException){
                Log.e(Tags.MainViewModelTag.toString(), "Location permission not granted")
            }

        }
    }

    fun changeNavigationState(newDestination: BottomBarDestination){
        navigationState = navigationState.copy(currentDestination = newDestination)
    }

    private fun collectLoginStatusFlow(){
        viewModelScope.launch {
            delay(500L)
            loginStatusFlow.collect {loginStatus ->
                when(loginStatus){
                    LoginStatus.None -> {}
                    LoginStatus.Success -> {
                        authState = authState.copy(showInvalidPasswordPrompt = false)
                    }
                    LoginStatus.Fail -> {
                        authState = authState.copy(showInvalidPasswordPrompt = true)
                        authState = authState.copy(
                            loginLogin = "",
                            loginPassword = ""
                        )
                    }
                    LoginStatus.Processing -> {Log.d(Tags.MainViewModelTag.toString(), "Logging in...")}  // TODO show a loading icon or smth
                }
            }
        }
    }

    fun getLatestToilets(){
        Log.d(Tags.MainViewModelTag.toString(), "Getting latest toilets")

        viewModelScope.launch {
            val toilets = repository.retrieveToilets()
            Log.d(Tags.MainViewModelTag.toString(), "Got api result")


            if (toilets == null){
                Log.e(Tags.MainViewModelTag.toString(), "Error getting toilets")
                return@launch
            }

            toiletsState = toiletsState.copy(toiletList = toilets)

            Log.d(Tags.MainViewModelTag.toString(), "Saved toilets $toilets")
            refreshToilets()

        }

    }

    fun navigateToDetails(toilet: Toilet, source: CurrentDetailsScreen){
        viewModelScope.launch {
            Log.d(Tags.MainViewModelTag.toString(), "Opening details: ${toilet.id}, $source")
            val author = repository.retrieveUserById(toilet.authorId)
            val authorName = author?.displayName ?: "Error"

            detailsState = detailsState.copy(
                toilet = toilet,
                currentDetailScreen = source,
                authorName = authorName
            )
        }
    }

    fun leaveDetailsScreen(){
        Log.d(Tags.MainViewModelTag.toString(), "Leaving details screen")
        detailsState = detailsState.copy(toilet = null, currentDetailScreen = CurrentDetailsScreen.NONE)
        Log.d(Tags.MainViewModelTag.toString(), "Left details: $detailsState")


    }
    private fun refreshToilets(){
        Log.d(Tags.MainViewModelTag.toString(), "Refreshing toilets")

        mapState = mapState.copy(toiletMarkers = toiletsState.toiletList.map { toilet ->  toToiletMarker(toilet)})
    }
    fun addToilet(){
        Log.d(Tags.MainViewModelTag.toString(), "Adding toilets")
    }
    fun navigateBackButton(){
        Log.d(Tags.MainViewModelTag.toString(), "Navigating back")
    }

    fun placeholder(){
        TODO("Placeholder Here")
    }


    fun login() {
        val login = authState.loginLogin
        val password = authState.loginPassword

        viewModelScope.launch {
            repository.login(login, password)
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
            clearAuthState()
        }
    }

    fun clearAuthState(){
        authState = authState.copy(
            status = AuthUiStatus.MAIN,
            registerLogin = "",
            registerPassword = "",
            registerPasswordConfirmation = "",
            registerName = "",
            loginLogin = "",
            loginPassword = "",
            showInvalidPasswordPrompt = false,
            registrationError = null
        )
    }

    private suspend fun registerUser(res: RegistrationError?){
        authState = authState.copy(registrationError = res)

        if (authState.registrationError == null) {
            repository.register(authState.registerLogin, authState.registerPassword, authState.registerName.trim())
        }
    }

    fun startRegister(){
        viewModelScope.launch {
            val error = validateRegistrationData()

            registerUser(error)
        }
    }


    private suspend fun validateRegistrationData(): RegistrationError? {
        val password = authState.registerPassword
        val login = authState.registerLogin

        if (authState.registerLogin.isBlank() ||
            authState.registerPassword.isBlank() ||
            authState.registerName.isBlank() ||
            authState.registerPasswordConfirmation.isBlank())
            return RegistrationError.EMPTY_FIELD

        if (authState.registerPassword != authState.registerPasswordConfirmation)
            return RegistrationError.PASSWORD_CONFIRMATION_MATCH


        if (password.length < MIN_PASSWORD_LENGTH)
            return RegistrationError.PASSWORD_TOO_SHORT


        if (!checkForNumbers(password))
            return RegistrationError.PASSWORD_CONTAINS_NO_NUMBERS


        if (!checkForUpperCase(password))
            return RegistrationError.PASSWORD_CONTAINS_NO_UPPERCASE


        if (!checkForLowerCase(password))
            return RegistrationError.PASSWORD_CONTAINS_NO_LOWERCASE


        if (login.contains(" "))
            return RegistrationError.LOGIN_CONTAINS_SPACES

        if (password.contains(" "))
            return RegistrationError.PASSWORD_CONTAINS_SPACES


        when(repository.checkIfLoginExists(login)) {
            true -> { return RegistrationError.LOGIN_ALREADY_TAKEN }
            false -> {}
            null -> { return RegistrationError.NETWORK_ERROR }
        }
        return null
    }

    fun continueWithoutAccount(){
        viewModelScope.launch {
            repository.continueWithoutLogin()
        }
    }
    private fun checkForNumbers(str: String): Boolean {
        for (char in str){
            if (char.isDigit()) return true
        }
        return false
    }
    private fun checkForUpperCase(str: String): Boolean {
        for (char in str){
            if (char.isUpperCase()) return true
        }
        return false
    }

    private fun checkForLowerCase(str: String): Boolean {
        for (char in str){
            if (char.isLowerCase()) return true
        }
        return false
    }

    fun setPadding(paddingValues: PaddingValues){
        scaffoldPadding = paddingValues
    }

    fun setAuthStatus(status: AuthUiStatus){
        authState = authState.copy(status = status)
    }

    fun getToilets(){
        viewModelScope.launch {
            val toilets = repository.retrieveToilets()
            toilets?.forEach{
                Log.d("ToiletLogger", it.toString())
            }
        }
    }
    fun getToiletById(id: Int){
        viewModelScope.launch {
            val toilet = repository.retrieveToiletById(id)

            Log.d("ToiletLogger", toilet.toString())
        }
    }

}


