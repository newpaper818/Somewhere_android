package com.newpaper.somewhere.feature.trip.invitedFriend.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.shimmerEffect
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.utils.itemMaxWidth

@Composable
internal fun LoadingCard(
    isLoading: Boolean
){
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(tween(0)),
        exit = fadeOut(tween(500))
    ) {
        Column(
            modifier = Modifier.widthIn(max = itemMaxWidth)
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )

            MySpacerColumn(height = 4.dp)

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .fillMaxWidth()
                    .height(245.dp)
                    .shimmerEffect()
            ) {

            }
        }
    }
}