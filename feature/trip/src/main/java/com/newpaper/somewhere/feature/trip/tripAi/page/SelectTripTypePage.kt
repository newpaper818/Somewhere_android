package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.tripAi.component.ErrorMessage
import com.newpaper.somewhere.feature.trip.tripAi.component.SelectButton
import com.newpaper.somewhere.feature.trip.tripAi.model.TripType

@Composable
internal fun SelectTripTypePage(
    internetEnabled: Boolean,
    selectedTripTypes: Set<TripType>,
    onClick: (TripType) -> Unit
){
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    Column {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                TripAiPage(
                    modifier = Modifier.padding(top = (screenHeight/5).dp),
                    title = stringResource(id = R.string.whats_your_trip_type),
                    subTitle = stringResource(id = R.string.select_multiple),
                    content = {
                        TripTypeList(
                            selectedTripTypes = selectedTripTypes,
                            onClick = onClick
                        )
                    }
                )
            }
        }

        ErrorMessage(
            visible = !internetEnabled,
            text = stringResource(id = R.string.internet_unavailable),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun TripTypeList(
    selectedTripTypes: Set<TripType>,
    onClick: (TripType) -> Unit
){
    val tripWithList = enumValues<TripType>()

    tripWithList.forEach { tripWithItem ->
        SelectButton(
            icon = tripWithItem.icon,
            text = stringResource(id = tripWithItem.textId),
            isSelected = tripWithItem in selectedTripTypes,
            onClick = {
                onClick(tripWithItem)
            }
        )

        if (tripWithItem != tripWithList.last()){
            MySpacerColumn(height = 16.dp)
        }
    }
}