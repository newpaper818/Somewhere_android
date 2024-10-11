package com.newpaper.somewhere.feature.trip.tripAi.model

import androidx.annotation.StringRes
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.TripTypeIcon
import com.newpaper.somewhere.core.model.R

enum class TripType(
    val icon: MyIcon,
    @StringRes val textId: Int
) {
    CULTURAL_EXPERIENCES(TripTypeIcon.culturalExperiences, R.string.trip_type_cultural_experiences),
    HISTORICAL_SITES(TripTypeIcon.historicalSites, R.string.trip_type_historical_sites),
    EXHIBITIONS_MUSEUMS(TripTypeIcon.exhibitionsMuseums, R.string.trip_type_exhibitions_museums),
    SPORTS_ACTIVITY(TripTypeIcon.sportsActivity, R.string.trip_type_sports_activity),
    GREAT_FOOD(TripTypeIcon.greatFood, R.string.trip_type_great_food),
    SHOPPING(TripTypeIcon.shopping, R.string.trip_type_shopping)
}