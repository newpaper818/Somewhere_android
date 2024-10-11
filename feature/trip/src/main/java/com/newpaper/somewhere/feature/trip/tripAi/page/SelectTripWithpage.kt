package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.trip.tripAi.model.TripWith
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.tripAi.component.SelectButton

@Composable
internal fun SelectTripWithPage(
    selectedTripWith: TripWith?,
    setTripWith: (TripWith) -> Unit
){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            TripAiPage(
                title = stringResource(id = R.string.who_are_you_traveling_with),
                subTitle = stringResource(id = R.string.select_one),
                content = {
                    TripWithList(
                        selectedTripWith = selectedTripWith,
                        onClick = setTripWith
                    )
                }
            )
        }
    }
}

@Composable
private fun TripWithList(
    selectedTripWith: TripWith?,
    onClick: (TripWith) -> Unit
){
    val tripWithList = enumValues<TripWith>()

    tripWithList.forEach { tripWithItem ->
        SelectButton(
            icon = tripWithItem.icon,
            text = stringResource(id = tripWithItem.textId),
            isSelected = tripWithItem == selectedTripWith,
            onClick = {
                onClick(tripWithItem)
            }
        )

        if (tripWithItem != tripWithList.last()){
            MySpacerColumn(height = 16.dp)
        }
    }
}