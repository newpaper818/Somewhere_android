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
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun CreateTripPage(

){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

            MySpacerColumn(height = 40.dp)
        }
    }

}