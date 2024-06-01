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
    SET_THEME("setTheme"),
    ACCOUNT("account"),
    EDIT_PROFILE("editProfile"),
    DELETE_ACCOUNT("deleteAccount"),
    ABOUT("about"),
    OPEN_SOURCE_LICENSE("openSourceLicense"),

    TRIP("trip"),
    DATE("date"),
    SPOT("spot"),

    INVITE_FRIEND("inviteFriend"),
    INVITED_FRIENDS("invitedFriends"),

    IMAGE("image"),
    SET_LOCATION("setLocation"),
    TRIP_MAP("tripMap")
}