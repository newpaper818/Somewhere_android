package com.newpaper.somewhere.feature.trip.invitedFriend.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun NoFriendItem(

){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ){
        Text(
            text = stringResource(id = R.string.no_friends),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}