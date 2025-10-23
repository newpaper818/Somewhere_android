package com.newpaper.somewhere.feature.trip.tripAi.model

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.TripWithIcon
import com.newpaper.somewhere.feature.trip.R

enum class TripWith(
    val icon: MyIcon,
    @StringRes val textId: Int
) {
    SOLO(TripWithIcon.solo, R.string.trip_with_solo),
    PARTNER(TripWithIcon.partner,R.string.trip_with_partner),
    FRIENDS(TripWithIcon.friends,R.string.trip_with_friends),
    FAMILY(TripWithIcon.family,R.string.trip_with_family)
}