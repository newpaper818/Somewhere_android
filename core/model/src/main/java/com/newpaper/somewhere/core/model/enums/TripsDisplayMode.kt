package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R

enum class TripsDisplayMode(
    @StringRes val textId: Int
) {
    ALL(R.string.all),
    /**Includes both ongoing and upcoming trips*/
    ACTIVE(R.string.active),
    PAST(R.string.past)
}