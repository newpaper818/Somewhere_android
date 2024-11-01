package com.newpaper.somewhere.feature.more.subscription.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.more.R

@Composable
internal fun NoticeText(
    
){
    Column(
        modifier = Modifier
            .widthIn(max = itemMaxWidthSmall)
            .padding(horizontal = 16.dp)
    ) {
        Description(text = stringResource(id = R.string.you_can_manage_and_cancel_on_google_play_store))
        MySpacerColumn(height = 8.dp)
        Description(text = stringResource(id = R.string.somewhere_pro_features_and_pricing_may_change_later))
    }
}

@Composable
private fun Description(
    text: String
){
    Row {
        Text(
            text = stringResource(id = R.string.dot),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MySpacerRow(width = 6.dp)

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}