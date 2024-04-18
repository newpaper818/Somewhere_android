package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.ProviderId

data class UserData(
    val userId: String,
    val userName: String?,
    val email: String?,
    val profileImage: String?,

    val providerIdList: List<ProviderId>,

    val allowEdit: Boolean = false
)