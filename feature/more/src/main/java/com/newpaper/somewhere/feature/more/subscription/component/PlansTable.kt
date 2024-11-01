package com.newpaper.somewhere.feature.more.subscription.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.feature.more.R

@Composable
internal fun PlansTable(
    freeTrips: Int,
    proTrips: Int,
    freeInviteFriends: Int,
    proInviteFriends: Int
) {
    MyCard(
        modifier = Modifier.width(300.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {

            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                PlansRow(
                    name = "",
                    freeValue = stringResource(id = R.string.free),
                    proValue = stringResource(id = R.string.pro),
                    isTop = true
                )
                PlansRow(
                    name = stringResource(id = R.string.ads),
                    freeValue = stringResource(id = R.string.o),
                    proValue = stringResource(id = R.string.x)
                )
                PlansRow(
                    name = stringResource(id = R.string.trips_up_to),
                    freeValue = freeTrips.toString(),
                    proValue = proTrips.toString()
                )
                PlansRow(
                    name = stringResource(id = R.string.invite_friends_up_to),
                    freeValue = freeInviteFriends.toString(),
                    proValue = proInviteFriends.toString()
                )
            }
        }
    }
}

@Composable
private fun PlansRow(
    name: String,
    freeValue: String,
    proValue: String,
    isTop: Boolean = false
){
    Row(
        modifier = Modifier.height(54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier.width(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = freeValue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isTop) FontWeight.Bold else FontWeight.Normal
            )
        }

        Box(
            modifier = Modifier.width(70.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = proValue,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isTop) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

