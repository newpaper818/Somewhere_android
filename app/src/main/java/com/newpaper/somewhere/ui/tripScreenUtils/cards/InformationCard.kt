package com.newpaper.somewhere.ui.tripScreenUtils.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcon
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

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

@Composable
private fun IconTextRow(
    isEditMode: Boolean,
    icon: MyIcon,

    text: String,
    textStyle: TextStyle,

    onClick: (() -> Unit)? = null
) {
    val enabled = isEditMode && onClick != null

    ClickableBox(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.height(48.dp),
        enabled = enabled,
        onClick = {
            onClick?.let { it() }
        }
    ) {
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

            if (enabled) {
                Spacer(modifier = Modifier.weight(1f))

                DisplayIcon(icon = MyIcons.clickableItem)
            }
        }
    }
}