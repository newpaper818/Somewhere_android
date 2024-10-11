package com.newpaper.somewhere.core.data.moshi.adaptor

import com.newpaper.somewhere.core.model.enums.SpotType
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

internal class SpotTypeAdaptor{
    @FromJson
    fun fromJson(json: String): SpotType {
        val isIn = enumValues<SpotType>().any { it.name == json }

        return if (isIn)
            SpotType.valueOf(json)
        else
            SpotType.TOUR
    }

    @ToJson
    fun toJson(value: SpotType): String {
        return value.name
    }
}