package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.DateFormat
import com.newpaper.somewhere.core.model.enums.TimeFormat

data class DateTimeFormat(
    val timeFormat: TimeFormat = TimeFormat.T24H,
    val useMonthName: Boolean = false,
    val includeDayOfWeek: Boolean = false,
    val dateFormat: DateFormat = DateFormat.YMD
)