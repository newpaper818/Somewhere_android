package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R

enum class AppTheme(
    @StringRes val textId: Int
) {
    LIGHT(R.string.theme_light),
    DARK(R.string.theme_dark),
    AUTO(R.string.theme_auto);

    // int -> Theme
    companion object {
        fun get(ordinal: Int) = entries[ordinal]
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
        fun get(ordinal: Int) = entries[ordinal]
    }
}