package com.example.somewhere.ui.tripScreenUtils.cards

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
import androidx.compose.ui.unit.dp
import com.example.somewhere.model.Date
import com.example.somewhere.enumUtils.SpotTypeGroup
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

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
                    SpotTypeGroupCard(
                        frontText = "$spotTypeCount ",
                        spotTypeGroup = it.first,
                        selected = it.second,
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