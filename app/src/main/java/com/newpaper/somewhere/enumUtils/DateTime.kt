package com.newpaper.somewhere.enumUtils

import androidx.annotation.StringRes
import com.newpaper.somewhere.R


enum class DateFormat(
    @StringRes val textId: Int
) {
    YMD(R.string.ymd),
    DMY(R.string.dmy),
    MDY(R.string.mdy);

    companion object {
        fun get(ordinal: Int) = DateFormat.values()[ordinal]
    }
}

enum class TimeFormat(
    @StringRes val textId: Int
)  {
    T12H(R.string._12h),
    T24H(R.string._24h);
    companion object {
        fun get(ordinal: Int) = TimeFormat.values()[ordinal]
    }
}