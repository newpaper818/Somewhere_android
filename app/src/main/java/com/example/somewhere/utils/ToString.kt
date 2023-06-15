package com.example.somewhere.utils

import java.text.DecimalFormat
import java.time.LocalDate

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
fun getDateText(date: LocalDate, includeYear: Boolean = true): String{
    val df1 = DecimalFormat("00")

    val year = date.year
    val month = df1.format(date.monthValue)
    val day = df1.format(date.dayOfMonth)

    return if (includeYear)
        "${year}.${month}.${day}"
    else
        "${month}.${day}"
}