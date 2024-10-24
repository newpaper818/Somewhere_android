package com.newpaper.somewhere.core.designsystem.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.model.enums.SpotTypeGroup
import com.newpaper.somewhere.core.model.tripData.Date
import com.newpaper.somewhere.core.ui.designsystem.R
import com.newpaper.somewhere.core.utils.convert.getSpotTypeGroupCount


@Composable
fun FilterButtons(
    date: Date,
    spotTypeWithShownList: List<Pair<SpotTypeGroup, Boolean>>,
    onCardClicked: (SpotTypeGroup) -> Unit,

    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
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

    onCardClicked: (SpotTypeGroup) -> Unit,

    frontText: String = "",
){
    val buttonColor =
        if (selected)    selectedColor ?: Color(spotTypeGroup.color.color)
        else             notSelectedColor

    val border =
        if (selected)   null
        else            BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)

    val textStyle =
        if (selected) {
            if (selectedColor == null) MaterialTheme.typography.labelLarge.copy(color = Color(spotTypeGroup.color.onColor))
            else                    MaterialTheme.typography.labelLarge
        }
        else    MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    val selectedText = stringResource(id = R.string.selected)
    val notSelectedText = stringResource(id = R.string.not_selected)
    val toggle = stringResource(id = R.string.toggle)

    Button(
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        border = border,
        contentPadding = PaddingValues(12.dp, 0.dp, 12.dp, 1.dp),
        onClick = { onCardClicked(spotTypeGroup)},
        modifier = Modifier.semantics {
            stateDescription = if (selected) selectedText else notSelectedText
            onClick(
                label = toggle,
                action = {
                    onCardClicked(spotTypeGroup)
                    true
                }
            )
        }
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



















@Composable
@PreviewLightDark
private fun SpotTypeGroupCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(100.dp)
        ) {
            SpotTypeGroupCard(
                spotTypeGroup = SpotTypeGroup.TOUR,
                selected = true,
                onCardClicked = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SpotTypeGroupCardPreview1(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(100.dp)
        ) {
            SpotTypeGroupCard(
                spotTypeGroup = SpotTypeGroup.FOOD,
                selected = true,
                onCardClicked = {},
                frontText = "3 "
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SpotTypeGroupCardPreview2(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(100.dp)
        ) {
            SpotTypeGroupCard(
                spotTypeGroup = SpotTypeGroup.MOVE,
                selected = true,
                onCardClicked = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun SpotTypeGroupCardPreview3(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp).width(100.dp)
        ) {
            SpotTypeGroupCard(
                spotTypeGroup = SpotTypeGroup.MOVE,
                selected = false,
                onCardClicked = {}
            )
        }
    }
}