package com.newpaper.somewhere.core.firebase_common.model

data class UserIdWithSharedTrips(
    val userId: String,
    var sharedTrips: List<Any?>?
)