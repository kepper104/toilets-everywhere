package com.kepper104.toiletseverywhere.data

import com.google.android.gms.maps.model.LatLng
import com.kepper104.toiletseverywhere.domain.model.ApiToilet
import com.kepper104.toiletseverywhere.domain.model.ApiUser
import com.kepper104.toiletseverywhere.domain.model.Toilet
import com.kepper104.toiletseverywhere.domain.model.User
import com.kepper104.toiletseverywhere.presentation.ui.state.ToiletMarker
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

fun fromApiToilet(apiToilet: ApiToilet): Toilet{

    val values = apiToilet.coordinates_.trim('(', ')').split(',')

    val xStr = values[0].trim().toFloat()
    val yStr = values[1].trim().toFloat()

    val coords = Pair(xStr, yStr)

    val creationDate: LocalDate = LocalDate.parse(apiToilet.creation_date_, dateFormatter)

    val openingTime: LocalTime = LocalTime.parse(apiToilet.opening_time_, timeFormatter)
    val closingTime: LocalTime = LocalTime.parse(apiToilet.closing_time_, timeFormatter)

    return Toilet(
        id = apiToilet.id_,
        authorId = apiToilet.author_id_,
        coordinates = coords,
        placeName = apiToilet.place_name_,
        isPublic = apiToilet.is_public_,
        disabledAccess = apiToilet.disabled_access_,
        babyAccess = apiToilet.baby_access_,
        parkingNearby = apiToilet.parking_nearby_,
        creationDate = creationDate,
        openingTime = openingTime,
        closingTime = closingTime,
        cost = apiToilet.cost_
    )
}

fun toToiletMarker(toilet: Toilet): ToiletMarker{
    return ToiletMarker(
        id = toilet.id,
        position = LatLng(toilet.coordinates.first.toDouble(), toilet.coordinates.second.toDouble()),
        rating = 0f, // TODO get rating from separate api request
        isPublic = toilet.isPublic,
        toilet = toilet
    )
}

fun fromApiUser(apiUser: ApiUser): User{
    val creationDate: LocalDate = LocalDate.parse(apiUser.creation_date_, dateFormatter)

    return User(
        id = apiUser.id_,
        displayName = apiUser.display_name_,
        creationDate = creationDate
    )
}