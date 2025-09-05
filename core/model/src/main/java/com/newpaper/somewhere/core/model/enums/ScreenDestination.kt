package com.newpaper.somewhere.core.model.enums

enum class ScreenDestination(
    val route: String
){
    SIGN_IN("signIn"),

//    TOP_LEVEL("topLevel"),

    TRIPS("trips"),
    PROFILE("profile"),
    MORE("more"),

    SET_DATE_TIME_FORMAT("setDateTimeFormat"),
    SET_SCREEN_STYLE("setScreenStyle"),
    ACCOUNT("account"),
    EDIT_PROFILE("editProfile"),
    SUBSCRIPTION("subscription"),
    DELETE_ACCOUNT("deleteAccount"),
    ABOUT("about"),
    OPEN_SOURCE_LICENSE("openSourceLicense"),

    TRIP_AI("tripAi"),
    TRIP("trip"),
    DATE("date"),
    SPOT("spot"),

    SHARE_TRIP("shareTrip"),

    INVITE_FRIEND("inviteFriend"),
    INVITED_FRIENDS("invitedFriends"),

    IMAGE("image"),
    SET_LOCATION("setLocation"),
    TRIP_MAP("tripMap")
}