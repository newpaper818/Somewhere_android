package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall

@Composable
internal fun TripAiPage(
    title: String,
    subTitle: String,
    content: @Composable () -> Unit,

    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .widthIn(max = itemMaxWidthSmall)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //title
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge
        )

        MySpacerColumn(height = 12.dp)

        //subtitle
        Text(
            text = subTitle,
            style = MaterialTheme.typography.bodyMedium
        )

        MySpacerColumn(height = 32.dp)

        //content
        content()
    }
}