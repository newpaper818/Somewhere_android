package com.example.somewhere.ui.screenUtils.cards

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcon
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun InformationCard(
    //              icon    text
    list: List<Pair<MyIcon, String?>>,

    modifier: Modifier = Modifier,

    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY)
){
    //information card
    Card(
        elevation = 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            list.forEach{
                if (it.second != null) {
                    IconTextRow(
                        isEditMode = false,
                        icon = it.first,
                        text = it.second!!,
                        textStyle = textStyle
                    )
                }
            }
        }
    }
}

@Composable
fun InformationCard(
    isEditMode: Boolean,
    //                icon    text     onclick
    list: List<Triple<MyIcon, String?, (() -> Unit)?>>,

    modifier: Modifier = Modifier,

    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY)
){
    //information card
    Card(
        elevation = 0.dp,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            list.forEach{
                if (it.second != null) {
                    IconTextRow(
                        isEditMode = isEditMode && it.third != { }, //FIXME ??
                        icon = it.first,
                        text = it.second!!,
                        textStyle = textStyle,
                        onClick = it.third
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun IconTextRow(
    isEditMode: Boolean,
    icon: MyIcon,

    text: String,
    textStyle: TextStyle,

    onClick: (() -> Unit)? = null
) {
    Card (
        elevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .height(48.dp),
        backgroundColor = Color.Transparent,
        enabled = isEditMode && onClick != null,
        onClick = {
            onClick?.let { it() }
        },

    ){
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(30.dp),
                contentAlignment = Alignment.Center
            ) {
                DisplayIcon(icon = icon)
            }

            MySpacerRow(width = 16.dp)

            Text(
                text = text,
                style = textStyle
            )
        }
    }
}