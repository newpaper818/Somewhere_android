package com.newpaper.somewhere.core.data.moshi.adaptor

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDate

internal class LocalDateAdaptor {
    @FromJson
    fun fromJson(json: String): LocalDate {
        //2024-10-02 -> LocalDate
        return LocalDate.parse(json)
    }

    @ToJson
    fun toJson(value: LocalDate): String {
        return value.toString()
    }
}