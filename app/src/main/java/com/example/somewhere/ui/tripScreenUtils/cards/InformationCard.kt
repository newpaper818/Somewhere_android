package com.example.somewhere.ui.tripScreenUtils.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcon
import com.example.somewhere.ui.theme.Shapes
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
        modifier = modifier.fillMaxWidth()
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
        modifier = modifier.fillMaxWidth()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconTextRow(
    isEditMode: Boolean,
    icon: MyIcon,

    text: String,
    textStyle: TextStyle,

    onClick: (() -> Unit)? = null
) {
    ClickableBox(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.height(48.dp),
        enabled = isEditMode && onClick != null,
        onClick = {
            onClick?.let { it() }
        }
    ) {
//
//    }
//    Card (
//        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
//        shape = MaterialTheme.shapes.small,
//        modifier = Modifier.height(48.dp),
//        enabled = isEditMode && onClick != null,
//        onClick = {
//            onClick?.let { it() }
//        },
//
//    ){
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