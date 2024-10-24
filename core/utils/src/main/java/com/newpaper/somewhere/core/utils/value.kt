package com.newpaper.somewhere.core.utils

import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

//value.kt

const val PRIVACY_POLICY_KOR_URL = "https://lily-trouser-5da.notion.site/Somewhere-12808d9ba98480c8b668cbd398631ae7?pvs=73"
const val PRIVACY_POLICY_ENG_URL = "http://lily-trouser-5da.notion.site"

const val FEEDBACK_URL = "https://forms.gle/2UqNgmLqPdECiSb17"
const val BUG_REPORT_URL = "https://forms.gle/5XZSxD6xPuLAeXah7"

const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.newpaper.somewhere"






const val AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/6300978111"
const val AD_UNIT_ID = "ca-app-pub-9435484963354123/6706048530"

const val OAUTH_WEB_CLIENT_ID = "141125479046-n0dciq6m90m841sq88coqkct8qdm3pk1.apps.googleusercontent.com"




val itemMaxWidth = 700.dp
val itemMaxWidthSmall = 500.dp
val listItemHeight = 56.dp

const val MAX_FRIEND_CNT = 35





/**seoul location, initial location*/
val SEOUL_LOCATION = LatLng(37.55, 126.98)

/**10f, default map zoom level*/
const val DEFAULT_ZOOM_LEVEL = 10f

/**21f, max map zoom level*/
const val MAX_ZOOM_LEVEL = 21f

/**3f, minimum map zoom level*/
const val MIN_ZOOM_LEVEL = 3f

/**300ms, map move spot animation duration*/
const val ANIMATION_DURATION_MS = 300

/**200, map LatLng bound padding value*/
const val LAT_LNG_BOUND_PADDING = 200