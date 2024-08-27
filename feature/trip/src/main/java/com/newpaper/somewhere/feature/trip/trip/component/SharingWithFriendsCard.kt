package com.newpaper.somewhere.feature.trip.trip.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.MAX_FRIEND_CNT
import com.newpaper.somewhere.feature.trip.R

@Composable
internal fun SharingWithFriendsCard(
    trip: Trip,
    userIsManager: Boolean,
    internetEnabled: Boolean,
    isEditMode: Boolean,
    onClickInvitedFriends: () -> Unit,
    onClickShareTrip: () -> Unit
){
    AnimatedVisibility(
        visible = !isEditMode,
        enter = scaleIn(animationSpec = tween(300))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(300))
                + fadeOut(animationSpec = tween(300))
    ) {
        Column {
            MyCard(
                onClick = onClickInvitedFriends
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 8.dp, 8.dp)
                        .height(40.dp)
                ) {
                    Text(text = stringResource(id = R.string.trip_mates))
                    MySpacerRow(width = 16.dp)

                    //
                    DisplayIcon(icon = MyIcons.friends)
                    MySpacerRow(width = 4.dp)
                    Text(
                        text = "${trip.sharingTo.size}",
                        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    //invite friend button
                    if (userIsManager && trip.sharingTo.size < MAX_FRIEND_CNT && internetEnabled) {
                        MyPlainTooltipBox(tooltipText = stringResource(id = MyIcons.inviteFriend.descriptionTextId)) {
                            ClickableBox(
                                modifier = Modifier.size(40.dp),
                                shape = MaterialTheme.shapes.small,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentAlignment = Alignment.Center,
                                onClick = onClickShareTrip
                            ) {
                                DisplayIcon(icon = MyIcons.inviteFriend)
                            }
                        }
                    }
                }
            }

            MySpacerColumn(16.dp)
        }
    }
}