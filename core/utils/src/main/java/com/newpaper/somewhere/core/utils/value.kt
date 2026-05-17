package com.newpaper.somewhere.core.utils

import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.newpaper.somewhere.core.utils.BuildConfig

//value.kt

//privacy policy url
const val PRIVACY_POLICY_KOR_URL = "https://lily-trouser-5da.notion.site/Somewhere-12808d9ba98480c8b668cbd398631ae7"
const val PRIVACY_POLICY_ENG_URL = "http://lily-trouser-5da.notion.site"

//feedback, bug report url
const val FEEDBACK_URL = "https://forms.gle/2UqNgmLqPdECiSb17"
const val BUG_REPORT_URL = "https://forms.gle/5XZSxD6xPuLAeXah7"

//play store url
const val SOMEWHERE_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.newpaper.somewhere"

//github url
const val GITHUB_URL = "https://github.com/newpaper818"

//manage subscription url
const val PLAY_STORE_SUBSCRIPTIONS_URL = "http://play.google.com/store/account/subscriptions"



//ad unit id
//banner
val BANNER_AD_UNIT_ID_TEST = BuildConfig.BANNER_AD_UNIT_ID_TEST
val BANNER_AD_UNIT_ID = BuildConfig.BANNER_AD_UNIT_ID

//rewarded interstitial
val REWARDED_AD_UNIT_ID_TEST = BuildConfig.REWARDED_AD_UNIT_ID_TEST
val REWARDED_AD_UNIT_ID = BuildConfig.REWARDED_AD_UNIT_ID



//size
/**700.dp*/
val itemMaxWidth = 700.dp
/**500.dp*/
val itemMaxWidthSmall = 500.dp
/**56.dp*/
val listItemHeight = 56.dp


//Free / Pro
const val FREE_CONTAINS_ADS = true
const val PRO_CONTAINS_ADS = false

/**10*/
const val FREE_MAX_TRIPS = 10
/**300*/
const val PRO_MAX_TRIPS = 300

/**5*/
const val FREE_MAX_INVITE_FRIENDS = 5
/**50*/
const val PRO_MAX_INVITE_FRIENDS = 50




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