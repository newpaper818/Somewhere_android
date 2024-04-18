package com.newpaper.somewhere.core.model.data

import com.newpaper.somewhere.core.model.enums.AppTheme
import com.newpaper.somewhere.core.model.enums.MapTheme

data class Theme(
    val appTheme: AppTheme = AppTheme.AUTO,
    val mapTheme: MapTheme = MapTheme.AUTO
)