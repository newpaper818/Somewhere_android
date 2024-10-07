package com.newpaper.somewhere.core.data.moshi.adaptor

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.ZonedDateTime


internal class ZonedDateTimeAdaptor {
    @FromJson
    fun fromJson(json: String): ZonedDateTime {
        //2024-03-07T06:50:36.453561Z -> ZonedDateTime
        return ZonedDateTime.parse(json)
    }

    @ToJson
    fun toJson(value: ZonedDateTime): String {
        return value.toString()
    }
}