package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.model.Date
import com.newpaper.somewhere.enumUtils.SpotTypeGroup
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.SPACER_SMALL
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.getTextStyle

@Composable
fun FilterCards(
    date: Date,
    spotTypeWithShownList: List<Pair<SpotTypeGroup, Boolean>>,
    onCardClicked: (SpotTypeGroup) -> Unit,

    startPadding: Dp = SPACER_SMALL,
    endPadding: Dp = SPACER_SMALL,
    textStyle: TextStyle = getTextStyle(TextType.CARD__SPOT_TYPE),
    isShownColor: Color = getColor(ColorType.BUTTON),
    isNotShownColor: Color = getColor(ColorType.CARD),
){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(startPadding, 0.dp, endPadding, 0.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        items(spotTypeWithShownList){

            val spotTypeCount = date.getSpotTypeGroupCount(it.first)

            if (spotTypeCount != 0) {
                Row{
                    SpotTypeGroupCard(
                        frontText = "$spotTypeCount ",
                        spotTypeGroup = it.first,
                        selected = it.second,
                        onCardClicked = {spotType ->
                            onCardClicked(spotType)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SpotTypeGroupCard(
    spotTypeGroup: SpotTypeGroup,

    selected: Boolean,

    selectedColor: Color? = null,
    notSelectedColor: Color = Color.Transparent,

    selectedTextStyle: TextStyle = getTextStyle(TextType.CARD__SPOT_TYPE),
    notSelectedTextStyle: TextStyle = getTextStyle(TextType.CARD__SPOT_TYPE).copy(color = MaterialTheme.colorScheme.onSurfaceVariant),

    onCardClicked: (SpotTypeGroup) -> Unit,

    frontText: String = "",
){
    val cardColor =
        if (selected)    selectedColor ?: Color(spotTypeGroup.color.color)
        else             notSelectedColor

    val border =
        if (selected)   null
        else            BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)

    val textStyle =
        if (selected) {
            if (selectedColor == null) selectedTextStyle.copy(color = Color(spotTypeGroup.color.onColor))
            else                    selectedTextStyle
        }
        else    notSelectedTextStyle

    Button(
        colors = ButtonDefaults.buttonColors(containerColor = cardColor),
        border = border,
        contentPadding = PaddingValues(12.dp, 0.dp, 12.dp, 2.dp),
        onClick = { onCardClicked(spotTypeGroup)}
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = frontText + stringResource(spotTypeGroup.textId),
                style = textStyle
            )
        }
    }
}