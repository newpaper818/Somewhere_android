package com.newpaper.somewhere.core.utils

import java.time.ZonedDateTime

fun getTripId(
    managerId: String,
    firstCreatedTripTime: ZonedDateTime
): Int {
    return managerId.hashCode() * 31 + firstCreatedTripTime.toInstant().toEpochMilli().hashCode()
}