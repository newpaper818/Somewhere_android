package com.newpaper.somewhere.feature.trip.tripAi.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.model.enums.TripType
import com.newpaper.somewhere.feature.trip.R
import com.newpaper.somewhere.feature.trip.tripAi.component.ErrorMessage
import com.newpaper.somewhere.feature.trip.tripAi.component.SelectButton

@Composable
internal fun SelectTripTypePage(
    internetEnabled: Boolean,
    selectedTripTypes: Set<TripType>,
    onClick: (TripType) -> Unit
){
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
            text = stringResource(id = R.string.internet_unavailable)
        )

        MySpacerColumn(height = 16.dp)
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