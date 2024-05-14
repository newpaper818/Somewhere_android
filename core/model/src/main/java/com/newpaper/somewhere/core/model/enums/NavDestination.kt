package com.newpaper.somewhere.core.model.enums

enum class NavDestination(
    val route: String
){
    SIGN_IN_ROUTE("signIn"),

    TRIPS_ROUTE("trips"),
    PROFILE_ROUTE("profile"),
    MORE_ROUTE("more"),

    SET_DATE_TIME_FORMAT_ROUTE("setDateTimeFormat"),
    SET_THEME_ROUTE("setTheme"),
    ACCOUNT_ROUTE("account"),
    EDIT_ACCOUNT("editAccount"),
    DELETE_ACCOUNT_ROUTE("deleteAccount"),
    ABOUT_ROUTE("about"),
    OPEN_SOURCE_LICENSE_ROUTE("openSourceLicense"),

    TRIP_ROUTE("trip"),
    DATE_ROUTE("date"),
    SPOT_ROUTE("spot"),

    INVITE_FRIEND_ROUTE("inviteFriend"),
    INVITED_FRIENDS_ROUTE("invitedFriends"),

    IMAGE_ROUTE("image"),
    SET_LOCATION_ROUTE("setLocation"),
    TRIP_MAP_ROUTE("tripMap")
}