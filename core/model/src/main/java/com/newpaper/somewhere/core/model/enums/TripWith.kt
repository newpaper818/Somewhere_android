package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R

enum class TripWith(
    @StringRes val textId: Int
) {
    SOLO(R.string.trip_with_solo),
    PARTNER(R.string.trip_with_partner),
    FRIENDS(R.string.trip_with_friends),
    FAMILY(R.string.trip_with_family)
}