package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.shimmerEffect
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.trip.trips.tripCardHeightDp

@Composable
internal fun LoadingTripsItem(

){
    Column {

        MySpacerColumn(height = 50.dp)

        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tripCardHeightDp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
            MySpacerColumn(height = 16.dp)
        }

    }
}