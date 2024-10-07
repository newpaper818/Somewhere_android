package com.newpaper.somewhere.core.data.moshi.adaptor

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalTime

internal class LocalTimeAdaptor {
    @FromJson
    fun fromJson(json: String): LocalTime {
        //13:30 -> LocalTime
        return LocalTime.parse(json)
    }

    @ToJson
    fun toJson(value: LocalTime): String {
        return value.toString()
    }
}