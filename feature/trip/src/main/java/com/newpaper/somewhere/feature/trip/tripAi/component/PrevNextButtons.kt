package com.newpaper.somewhere.feature.trip.tripAi.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.CreateTripButton
import com.newpaper.somewhere.core.designsystem.component.button.NextButton
import com.newpaper.somewhere.core.designsystem.component.button.PrevButton
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall

@Composable
internal fun PrevNextButtons(
    prevButtonVisible: Boolean,
    onClickPrev: () -> Unit,

    nextIsCreateTrip: Boolean,
    nextButtonEnabled: Boolean, //or create trip
    onClickNext: () -> Unit,

    modifier: Modifier = Modifier
){
    Box(modifier = modifier.widthIn(max = itemMaxWidthSmall)){
        Row(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
        ) {
            //prev button
            if (prevButtonVisible)
                PrevButton(onClick = onClickPrev)

            Spacer(modifier = Modifier.weight(1f))

            //next button
            if (!nextIsCreateTrip)
                NextButton(
                    onClick = onClickNext,
                    enabled = nextButtonEnabled
                )
            else
                CreateTripButton(
                    onClick = onClickNext,
                    enabled = nextButtonEnabled
                )
        }
    }
}