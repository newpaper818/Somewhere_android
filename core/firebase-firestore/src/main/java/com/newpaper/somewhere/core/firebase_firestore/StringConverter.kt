package com.newpaper.somewhere.core.firebase_firestore

import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.core.model.enums.SpotType
import java.time.LocalTime

/**
 * "-14503604/-16777216"
 *
 * -> [MyColor] (color = -14503604, onColor = -16777216)
 */
internal fun stringToMyColor(value: String): MyColor {
    val parts = value.split("/")
    return if (parts.size == 2) {
        val color = parts[0].toInt()
        val onColor = parts[1].toInt()

        MyColor(color, onColor)
    } else
        MyColor()
}

/**
 * "TOUR" -> [SpotType].TOUR
 */
internal fun stringToSpotType(value: String): SpotType {
    return try {
        SpotType.valueOf(value)
    }catch(e: IllegalArgumentException) {
        SpotType.TOUR
    }
}

/**
 * "13:40" -> [LocalTime].of(13, 40)
 *
 * null -> null
 */
internal fun stringToLocalTime(value: String?): LocalTime?{
    if (value == null)
        return null

    val parts = value.split(":")
    return if (parts.size == 2) {
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        LocalTime.of(hour, minute)
    } else
        null
}

/**
 * [LocalTime].of(13, 40) -> "13:40"
 *
 * null -> null
 */
internal fun localTimeToString(localTime: LocalTime?): String? {
    return if (localTime == null) null
    else "${localTime.hour}:${localTime.minute}"
}