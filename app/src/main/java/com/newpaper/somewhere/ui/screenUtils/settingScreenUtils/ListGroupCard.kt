package com.newpaper.somewhere.ui.screenUtils.settingScreenUtils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

@Composable
fun ListGroupCard(
    title: String? = null,
    titleTextStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_TITLE),
    content: @Composable() (() -> Unit)
) {
    Column {
        if (title != null) {
            Row {
                MySpacerRow(width = 16.dp)
                Text(
                    text = title,
                    style = titleTextStyle
                )
            }

            MySpacerColumn(height = 6.dp)
        }

        Card (modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun ItemDivider(
    horizontalPadding: Dp = 16.dp
){
    Row {
        MySpacerRow(width = horizontalPadding)
        Divider(modifier = Modifier.weight(1f))
        MySpacerRow(width = horizontalPadding)
    }
}