package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall

@Composable
internal fun TripAiPage(
    title: String,
    subTitle: String,
    content: @Composable () -> Unit,
    onClickClose: () -> Unit,

    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .widthIn(max = itemMaxWidthSmall)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            CloseButton(
                onClick = onClickClose
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
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
            }
        }


        MySpacerColumn(height = 32.dp)

        //content
        content()
    }
}

@Composable
private fun CloseButton(
    onClick: () -> Unit
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceDim)
            .clickable { onClick() }
    ) {
        DisplayIcon(icon = TopAppBarIcon.close)
    }
}