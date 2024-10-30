package com.newpaper.somewhere.feature.more.subscription.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.utils.itemMaxWidthSmall
import com.newpaper.somewhere.feature.more.R

@Composable
internal fun AdditionalDescription(
    
){
    Column(
        modifier = Modifier.widthIn(max = itemMaxWidthSmall)
    ) {
        Description(text = stringResource(id = R.string.you_can_cancel_on_google_play))
        MySpacerColumn(height = 4.dp)
        Description(text = stringResource(id = R.string.pro_features_can_be_change))
    }
}

@Composable
private fun Description(
    text: String
){
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}