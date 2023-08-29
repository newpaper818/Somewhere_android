package com.example.somewhere.ui.screenUtils.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.model.Date
import com.example.somewhere.typeUtils.SpotTypeGroup
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import com.example.somewhere.ui.theme.white

@Composable
fun FilterCards(
    date: Date,
    spotTypeWithShownList: List<Pair<SpotTypeGroup, Boolean>>,
    onCardClicked: (SpotTypeGroup) -> Unit,

    textStyle: TextStyle = getTextStyle(TextType.CARD__SPOT_TYPE),
    isShownColor: Color = getColor(ColorType.BUTTON),
    isNotShownColor: Color = getColor(ColorType.CARD),
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(16.dp, 0.dp, 4.dp, 0.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        items(spotTypeWithShownList){

            val spotTypeCount = date.getSpotTypeGroupCount(it.first)

            if (spotTypeCount != 0) {
                Row{
                    SpotTypeCard(
                        frontText = "$spotTypeCount ",
                        spotTypeGroup = it.first,
                        textStyle = textStyle,
                        isShown = it.second,
                        isShownColor = isShownColor,
                        isNotShownColor = isNotShownColor,
                        onCardClicked = {spotType ->
                            onCardClicked(spotType)
                        }
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SpotTypeCard(
    spotTypeGroup: SpotTypeGroup,
    textStyle: TextStyle,
    isShown: Boolean,
    isShownColor: Color,
    isNotShownColor: Color,
    onCardClicked: (SpotTypeGroup) -> Unit,
    frontText: String = ""
){
    val cardColor =
        if (isShown)    isShownColor
        else    isNotShownColor

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(32.dp),
        backgroundColor = cardColor,
        elevation = 0.dp,
        onClick = { onCardClicked(spotTypeGroup) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = frontText + stringResource(spotTypeGroup.textId),
                style = textStyle,
                modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 2.dp)
            )
        }
    }
}