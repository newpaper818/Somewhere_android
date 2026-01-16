package com.newpaper.somewhere.feature.trip.trip.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.newpaper.smooth_corner.SmoothRoundedCornerShape
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.enterVerticallyScaleInDelay
import com.newpaper.somewhere.core.utils.exitVerticallyScaleOut
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun ShareTripCards(
    trip: Trip,
    maxInviteFriends: Int,
    userIsManager: Boolean,
    internetEnabled: Boolean,
    isEditMode: Boolean,
    onClickInvitedFriends: () -> Unit,
    onClickAddFriend: () -> Unit,
    onClickShareTrip: () -> Unit,
    modifier: Modifier = Modifier
){
    AnimatedVisibility(
        visible = !isEditMode,
        modifier = modifier,
        enter = enterVerticallyScaleInDelay,
        exit = exitVerticallyScaleOut
    ) {
        Column {
            Row {
                TripMatesCard(
                    trip = trip,
                    maxInviteFriends = maxInviteFriends,
                    userIsManager = userIsManager,
                    internetEnabled = internetEnabled,
                    onClickInvitedFriends = onClickInvitedFriends,
                    onClickAddFriend = onClickAddFriend,
                    modifier = Modifier.weight(1f)
                )

                MySpacerRow(16.dp)

                ShareTripCard(
                    onClickShareTrip = onClickShareTrip,
                    modifier = Modifier.weight(1f)
                )
            }

            MySpacerColumn(16.dp)
        }
    }
}

@Composable
private fun TripMatesCard(
    trip: Trip,
    maxInviteFriends: Int,
    userIsManager: Boolean,
    internetEnabled: Boolean,
    onClickInvitedFriends: () -> Unit,
    onClickAddFriend: () -> Unit,
    modifier: Modifier = Modifier
){
    MyCard(
        onClick = onClickInvitedFriends,
        shape = SmoothRoundedCornerShape(999.dp, 1f),
        modifier = modifier.semantics { role =  Role.Button}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp, 8.dp, 8.dp, 8.dp)
                .height(40.dp)
        ) {
            DisplayIcon(icon = MyIcons.friends)
            MySpacerRow(width = 8.dp)

            Text(text = "${trip.sharingTo.size}")
            MySpacerRow(width = 4.dp)

            Text(text = stringResource(id = R.string.trip_mates))



//            MySpacerRow(width = 16.dp)

//            Spacer(modifier = Modifier.weight(1f))

            //invite friend button
//            if (userIsManager && trip.sharingTo.size < maxInviteFriends && internetEnabled) {
//                MyPlainTooltipBox(tooltipText = stringResource(id = MyIcons.inviteFriend.descriptionTextId!!)) {
//                    ClickableBox(
//                        modifier = Modifier.size(40.dp),
//                        shape = MaterialTheme.shapes.small,
//                        containerColor = MaterialTheme.colorScheme.surface,
//                        contentAlignment = Alignment.Center,
//                        onClick = onClickAddFriend
//                    ) {
//                        DisplayIcon(icon = MyIcons.inviteFriend)
//                    }
//                }
//            }
        }
    }
}


@Composable
private fun ShareTripCard(
    onClickShareTrip: () -> Unit,
    modifier: Modifier = Modifier
){
    MyCard(
        onClick = onClickShareTrip,
        shape = SmoothRoundedCornerShape(999.dp, 1f),
        modifier = modifier.semantics { role =  Role.Button}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp, 8.dp, 8.dp, 8.dp)
                .height(40.dp)
        ) {
            DisplayIcon(icon = MyIcons.shareTrip)
            MySpacerRow(width = 8.dp)
            Text(text = stringResource(id = R.string.share_trip))
        }
    }
}