package com.newpaper.somewhere.feature.trip.tripAi.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.CreateTripButton
import com.newpaper.somewhere.core.designsystem.component.button.NextButton
import com.newpaper.somewhere.core.designsystem.component.button.PrevButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.utils.itemMaxWidth

@Composable
internal fun PrevNextButtons(
    prevButtonVisible: Boolean,
    onClickPrev: () -> Unit,

    nextIsCreateTrip: Boolean,
    nextButtonEnabled: Boolean, //or create trip
    onClickNext: () -> Unit,

    errorVisible: Boolean,
    errorText: String,

    modifier: Modifier = Modifier
){
    Column {
        //gradation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            //error message
            AnimatedVisibility(
                visible = errorVisible,
                enter = expandVertically(tween(500)) + fadeIn(tween(500, 200)),
                exit = shrinkVertically(tween(500, 200)) + fadeOut(tween(500)),
            ) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error
                        )
                    )

                    MySpacerColumn(height = 4.dp)
                }
            }

            //buttons
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = itemMaxWidth)
                        .padding(16.dp, 2.dp, 16.dp, 8.dp)
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
    }
}