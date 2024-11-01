package com.newpaper.somewhere.core.utils.convert

import com.newpaper.somewhere.core.utils.FREE_CONTAINS_ADS
import com.newpaper.somewhere.core.utils.FREE_MAX_INVITE_FRIENDS
import com.newpaper.somewhere.core.utils.FREE_MAX_TRIPS
import com.newpaper.somewhere.core.utils.PRO_CONTAINS_ADS
import com.newpaper.somewhere.core.utils.PRO_MAX_INVITE_FRIENDS
import com.newpaper.somewhere.core.utils.PRO_MAX_TRIPS

fun getContainAds(
    isUsingSomewherePro: Boolean
): Boolean {
    return if (isUsingSomewherePro) PRO_CONTAINS_ADS
    else                            FREE_CONTAINS_ADS
}

fun getMaxTrips(
    isUsingSomewherePro: Boolean
): Int {
    return if (isUsingSomewherePro) PRO_MAX_TRIPS
    else                            FREE_MAX_TRIPS
}

fun getMaxInviteFriends(
    isUsingSomewherePro: Boolean
): Int {
    return if (isUsingSomewherePro) PRO_MAX_INVITE_FRIENDS
    else                            FREE_MAX_INVITE_FRIENDS
}