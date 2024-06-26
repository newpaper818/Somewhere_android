package com.newpaper.somewhere.feature.trip.trip.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun DateListEmptyTextCard(
    isEditMode: Boolean,
    enabledDateListIsEmpty: Boolean,
){
    val text =
        if (isEditMode) stringResource(id = R.string.set_trip_duration)
        else stringResource(id = R.string.dates_no_plan)

    AnimatedVisibility(
        visible = enabledDateListIsEmpty,
        enter = expandVertically(tween(500)),
        exit = shrinkVertically(tween(500))
    ) {
        MyCard(
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(16.dp, 0.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}