package com.newpaper.somewhere.core.model.enums

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.model.R


enum class DateFormat(
    @StringRes val textId: Int
) {
    YMD(R.string.ymd),
    DMY(R.string.dmy),
    MDY(R.string.mdy);

    companion object {
        fun get(ordinal: Int) = entries[ordinal]
    }
}

enum class TimeFormat(
    @StringRes val textId: Int
)  {
    T12H(R.string._12h),
    T24H(R.string._24h);
    companion object {
        fun get(ordinal: Int) = entries[ordinal]
    }
}
