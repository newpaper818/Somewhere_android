package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.TryAgainButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun CreateTripPage(
    internetEnabled: Boolean,
    createTripError: Boolean,
    onClickTryAgain: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!createTripError) {
                CreatingTrip()
            }
            else {
                Error(
                    internetEnabled = internetEnabled,
                    onClickTryAgain = onClickTryAgain
                )
            }

            MySpacerColumn(height = 40.dp)
        }
    }
}

@Composable
private fun CreatingTrip(

){
    Text(
        text = stringResource(id = R.string.creating_trip_with_ai),
        style = MaterialTheme.typography.displayLarge.copy(
            fontWeight = FontWeight.Normal
        )
    )

    MySpacerColumn(height = 8.dp)

    Text(
        text = stringResource(id = R.string.it_mat_took_a_while),
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun Error(
    internetEnabled: Boolean,
    onClickTryAgain: () -> Unit
){
    Text(
        text = stringResource(id = R.string.something_went_wrong),
        style = MaterialTheme.typography.bodyLarge
    )

    MySpacerColumn(height = 16.dp)

    TryAgainButton(
        enabled = internetEnabled,
        onClick = onClickTryAgain
    )
}