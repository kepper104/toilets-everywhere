package com.kepper104.toiletseverywhere.presentation.ui.state

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.kepper104.toiletseverywhere.domain.model.Toilet

data class MapState (
    val properties: MapProperties = MapProperties(isMyLocationEnabled = true),
    val cameraPosition: CameraPosition = CameraPosition(LatLng(55.80344037191546, 37.409658491929854), 10F, 0F, 0F),
    val toiletMarkers: List<ToiletMarker> = emptyList()
)

data class ToiletMarker(
    val id: Int = 0,
    val position: LatLng = LatLng(0.0, 0.0),
    val rating: Float = 0F,
    val isPublic: Boolean = true,
    val toilet: Toilet = Toilet()
)