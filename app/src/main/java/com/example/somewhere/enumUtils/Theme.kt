package com.example.somewhere.enumUtils

import androidx.annotation.StringRes
import com.example.somewhere.R

enum class AppTheme(
    @StringRes val textId: Int
) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    AUTO(R.string.theme_auto);

    // int -> Theme
    companion object {
        fun get(ordinal: Int) = values()[ordinal]
    }
}

enum class MapTheme(
    @StringRes val textId: Int
) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    AUTO(R.string.map_theme_auto);

    // int -> Theme
    companion object {
        fun get(ordinal: Int) = values()[ordinal]
    }
}