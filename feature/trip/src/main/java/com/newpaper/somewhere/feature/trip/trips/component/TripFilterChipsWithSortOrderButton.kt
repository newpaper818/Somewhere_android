package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.FilterChipButton
import com.newpaper.somewhere.core.designsystem.component.button.ToggleSortOrderButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.TripsDisplayMode


@Composable
fun TripFilterChipsWithSortOrderButton(
    tripsDisplayMode: TripsDisplayMode,
    onClickTripsDisplayMode: (TripsDisplayMode) -> Unit,

    isOrderByLatest: Boolean,
    onClickSortOrder: () -> Unit,

    modifier: Modifier = Modifier
){

    Row(
        modifier = modifier
    ) {
        LazyRow(
            modifier = Modifier.weight(1f)
        ) {
            //filter chips
            items(TripsDisplayMode.entries) {
                FilterChipButton(
                    text = stringResource(it.textId),
                    selected = it == tripsDisplayMode,
                    onClick = { onClickTripsDisplayMode(it) }
                )
                MySpacerRow(6.dp)
            }
        }

        //change order button
        ToggleSortOrderButton(
            isOrderByLatest = isOrderByLatest,
            onClick = onClickSortOrder
        )
    }
}






















@Composable
@PreviewLightDark
private fun FilterChipButtonPreview() {
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(300.dp)
        ) {
            TripFilterChipsWithSortOrderButton(
                tripsDisplayMode = TripsDisplayMode.ALL,
                onClickTripsDisplayMode = {},
                isOrderByLatest = true,
                onClickSortOrder = {}
            )
        }
    }
}