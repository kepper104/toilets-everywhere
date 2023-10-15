package com.kepper104.toiletseverywhere.presentation.ui.state

import com.google.android.gms.maps.model.LatLng

data class NewToiletDetailsState(
    val enabled: Boolean = false,
    val coordinates: LatLng = LatLng(0.0, 0.0),



)