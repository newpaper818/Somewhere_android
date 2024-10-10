package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn

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
                text = "Creating a trip with AI...",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Normal
                )
            )

            MySpacerColumn(height = 8.dp)

            Text(
                text = "It may took a while"
            )

            MySpacerColumn(height = 40.dp)
        }
    }

}