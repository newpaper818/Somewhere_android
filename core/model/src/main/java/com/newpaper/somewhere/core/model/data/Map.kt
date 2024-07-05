package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.tripData.Date

data class DateWithBoolean(
    val date: Date,
    val isShown: Boolean
)

data class SpotTypeGroupWithBoolean(
    val spotTypeGroup: SpotTypeGroup,
    val isShown: Boolean
)