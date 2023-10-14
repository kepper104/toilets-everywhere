package com.kepper104.toiletseverywhere.presentation.ui.state

import com.kepper104.toiletseverywhere.data.NOT_LOGGED_IN_STRING

data class LoggedInUserState (
    val isLoggedIn: Boolean = false,
    val currentUserName: String = NOT_LOGGED_IN_STRING,
    )