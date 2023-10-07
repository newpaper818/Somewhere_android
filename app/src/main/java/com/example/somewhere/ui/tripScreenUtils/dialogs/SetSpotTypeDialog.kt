package com.example.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.enumUtils.SpotType
import com.example.somewhere.enumUtils.SpotTypeGroup
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun SetSpotTypeDialog(
    initialSpotType: SpotType,
    onDismissRequest: () -> Unit,
    onOkClick: (SpotType) -> Unit,

    defaultTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY_SMALL),
    selectedCardColor: Color = getColor(ColorType.CARD_SELECTED),
    selectedTextStyle: TextStyle = getTextStyle(TextType.DIALOG__BODY_SMALL).copy(color = getColor(
        ColorType.CARD_ON_SELECTED
    ), fontWeight = FontWeight.Bold
    )
){
    val spotTypeGroupList = enumValues<SpotTypeGroup>()
    val spotTypeList = enumValues<SpotType>()

    var currentSpotTypeGroup by rememberSaveable { mutableStateOf(initialSpotType.group) }
    var currentSpotType by rememberSaveable { mutableStateOf(initialSpotType) }

    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,

        titleText = stringResource(id = R.string.dialog_title_set_spot_type),
        bodyContent = {

            //spot type group row list
            LazyRow(
                modifier = Modifier
                    //.height(400.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
                    .padding(0.dp, 8.dp)
            ) {

                item {
                    MySpacerRow(width = 8.dp)
                }

                items(spotTypeGroupList) {
                    val cardColor = if (it == currentSpotTypeGroup) selectedCardColor
                    else Color.Transparent

                    val cardTextStyle = if (it == currentSpotTypeGroup) selectedTextStyle
                    else defaultTextStyle

                    ClickableBox(
                        shape = MaterialTheme.shapes.small,
                        containerColor = cardColor,
                        modifier = Modifier.height(36.dp),
                        onClick = {
                            currentSpotTypeGroup = it

                            for (spotType in spotTypeList) {
                                if (spotType.group == it) {
                                    currentSpotType = spotType
                                    break
                                }
                            }
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(12.dp, 4.dp)
                        ) {
                            Text(
                                text = stringResource(id = it.textId),
                                style = cardTextStyle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.widthIn(40.dp)
                            )
                        }
                    }
                    MySpacerRow(width = 8.dp)
                }
            }

            MySpacerColumn(height = 8.dp)

            Column(
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(getColor(ColorType.DIALOG__CARD))
            ) {
                //spot type list
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(spotTypeList) {
                        if (it.group == currentSpotTypeGroup) {

                            val cardColor = if (it == currentSpotType) selectedCardColor
                            else Color.Transparent

                            val cardTextStyle = if (it == currentSpotType) selectedTextStyle
                            else defaultTextStyle

                            ClickableBox(
                                containerColor = cardColor,
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .height(46.dp)
                                    .fillMaxWidth(),
                                onClick = {
                                    currentSpotType = it
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp, 0.dp)
                                ) {
                                    Text(
                                        text = stringResource(id = it.textId),
                                        style = cardTextStyle,
                                    )
                                }

                            }
                        }
                    }
                }
            }
        },
        buttonContent = {
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
                    onClick = {
                        onOkClick(currentSpotType)
                    }
                )
            }
        }
    )


}