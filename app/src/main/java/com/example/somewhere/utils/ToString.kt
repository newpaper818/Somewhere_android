package com.example.somewhere.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import com.example.somewhere.enumUtils.DateFormat
import com.example.somewhere.enumUtils.TimeFormat
import com.example.somewhere.viewModel.DateTimeFormat
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

//1234567.12 -> "1,234,567.12" / 1,234 -> "1,234.00"
fun getNumToText(number: Float, numberOfDecimalPlaces: Int): String{
    var pattern = "#,##0"

    if (numberOfDecimalPlaces != 0){
        pattern += "."
        repeat(numberOfDecimalPlaces){
            pattern += "0"
        }
    }

    val numberFormat = DecimalFormat(pattern)

    return numberFormat.format(number)
}

//LocalDate -> "2023.06.12"
fun getDateText(
    date: LocalDate,
    dateTimeFormat: DateTimeFormat,
    includeYear: Boolean = true,
): String{
    val df0 = DecimalFormat("0")

    //year
    var year = date.year.toString()
    if (!dateTimeFormat.useMonthName) year = "$year."

    //month
    var month = if (dateTimeFormat.useMonthName) date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                else df0.format(date.monthValue)

    if (!dateTimeFormat.useMonthName) month = "$month."

    //day
    var day = df0.format(date.dayOfMonth)
    if (!dateTimeFormat.useMonthName) day = "$day."


    //day of week
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)


    var text =
        when (dateTimeFormat.dateFormat) {
            DateFormat.YMD -> if(includeYear) "$year $month $day"   else "$month $day"
            DateFormat.DMY -> if(includeYear) "$day $month $year"   else "$day $month"
            DateFormat.MDY -> if(includeYear) "$month $day $year"   else "$month $day"
        }

    if (text.last() == '.')
        text = text.dropLast(1)

    if (dateTimeFormat.includeDayOfWeek)
        text = "$text $dayOfWeek"


    return text
}

fun getTimeText(
    time: LocalTime,
    timeFormat: TimeFormat
): String {
    val df1 = DecimalFormat("00")

    val hour24 = time.hour
    val hour12 = if (hour24 > 12) hour24 - 12
                    else            hour24
    val minute = df1.format(time.minute)

    val isPm = time.isAfter(LocalTime.of(12,0))
    val amPm = if (isPm) "PM"   else "AM"

    return if (timeFormat == TimeFormat.T24H) "$hour24:$minute"
            else                               "$hour12:$minute $amPm"
}

@Composable
@ReadOnlyComposable
fun getLocale(): Locale? {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
}