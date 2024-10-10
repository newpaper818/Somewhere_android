package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R

enum class TripType(
    @StringRes val textId: Int
) {
    CULTURAL_EXPERIENCES(R.string.trip_type_cultural_experiences),
    ACTIVITY(R.string.activity),
    GREAT_FOOD(R.string.trip_type_great_food),
    SHOPPING(R.string.trip_type_shopping)
}