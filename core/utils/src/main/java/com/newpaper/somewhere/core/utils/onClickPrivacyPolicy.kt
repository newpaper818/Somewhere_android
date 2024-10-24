package com.newpaper.somewhere.core.utils

import androidx.compose.ui.platform.UriHandler
import java.util.Locale

fun onClickPrivacyPolicy(
    uriHandler: UriHandler
){
    val language = Locale.getDefault().language

    when (language){
        "ko" -> uriHandler.openUri(PRIVACY_POLICY_KOR_URL)
        "en" -> uriHandler.openUri(PRIVACY_POLICY_ENG_URL)
        else -> uriHandler.openUri(PRIVACY_POLICY_ENG_URL)
    }
}
