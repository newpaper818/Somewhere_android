package com.example.somewhere.enumUtils

import androidx.annotation.StringRes
import com.example.somewhere.R


enum class DateFormat(
    @StringRes val textId: Int
) {
    YMD(R.string.ymd),
    DMY(R.string.dmy),
    MDY(R.string.mdy),
}

enum class TimeFormat(
    @StringRes val textId: Int
)  {
    T24H(R.string._24h),
    T12H(R.string._12h)
}