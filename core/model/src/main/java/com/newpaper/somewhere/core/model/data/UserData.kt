package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.ProviderId

data class UserData(
    val userId: String,
    val userName: String?,
    val email: String?,
    val profileImagePath: String?,

    val providerIds: List<ProviderId>,

    val isUsingSomewherePro: Boolean = false,

    val allowEdit: Boolean = false
){
    val isGuest: Boolean
        get() = userId == GUEST_USERDATA.userId
}




val GUEST_USERDATA = UserData(
    userId = "guest-052615",
    userName = "Guest",
    email = "npsomewhere@gmail.com",
    profileImagePath = null,
    providerIds = listOf(),
    isUsingSomewherePro = false,
    allowEdit = false
)