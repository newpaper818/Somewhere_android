package com.newpaper.somewhere.feature.trip.trips.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.FilterChipButton
import com.newpaper.somewhere.core.designsystem.component.button.ToggleSortOrderButton
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.TripsDisplayMode


@Composable
fun TripFilterChipsWithSortOrderButton(
    spacerValue: Dp,

    tripsDisplayMode: TripsDisplayMode,
    onClickTripsDisplayMode: (TripsDisplayMode) -> Unit,

    isOrderByLatest: Boolean,
    onClickSortOrder: () -> Unit,

    modifier: Modifier = Modifier
){
    val density = LocalDensity.current
    var height by rememberSaveable { mutableIntStateOf(0) }


    Row(
        modifier = modifier
            .padding(end = spacerValue)
            .onSizeChanged{
                height = with(density) { it.height.toDp().value.toInt() }
            }
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = spacerValue + 12.dp, end = 48.dp)
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

            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(height.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
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
                spacerValue = 16.dp,
                tripsDisplayMode = TripsDisplayMode.ALL,
                onClickTripsDisplayMode = {},
                isOrderByLatest = true,
                onClickSortOrder = {}
            )
        }
    }
}