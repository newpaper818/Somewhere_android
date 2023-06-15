package com.example.somewhere.typeUtils

import androidx.annotation.StringRes
import com.example.somewhere.R

enum class SpotType(@StringRes val textId:Int) {
    TOUR(R.string.tour),
    MOVE(R.string.move),
    FOOD(R.string.food),
    ACCOMMODATION(R.string.accommodation),
    ETC(R.string.etc)
}