package com.newpaper.somewhere.feature.trip.trip.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.ui.card.trip.MAX_TITLE_LENGTH
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun DateListTopTitleCard(
    isEditMode: Boolean,
    dateListIsEmpty: Boolean,
    dateTitleErrorCount: Int
){
    AnimatedVisibility(
        visible = isEditMode,
        enter = expandVertically(tween(400)),
        exit = shrinkVertically(tween(400))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceBright)
        ) {
            Row(
                modifier = Modifier.padding(16.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.dates),
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = if (!dateListIsEmpty) Modifier.padding(
                        0.dp, 0.dp, 0.dp, 0.dp
                    )
                    else Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                )

                //up to 100 characters
                if (dateTitleErrorCount > 0) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = stringResource(
                            id = R.string.long_text,
                            MAX_TITLE_LENGTH
                        ),
                        style = MaterialTheme.typography.bodySmall.copy(color = CustomColor.outlineError)
                    )
                }
            }
        }
    }
}