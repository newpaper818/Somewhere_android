package com.example.somewhere.ui.settingScreenUtils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.somewhere.ui.commonScreenUtils.ClickableBox
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

@Composable
fun ItemWithText(
    body1Text: String?= null,
    body2Text: String? = null,

    isOpenInNew: Boolean = false,
    onItemClick: (() -> Unit)? = null,

    body1TextStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_ITEM_BODY1),
    body2TextStyle: TextStyle = getTextStyle(TextType.GROUP_CARD_ITEM_BODY2),
){
    ClickableBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        enabled = onItemClick != null,
        onClick = onItemClick ?: { }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 0.dp)
        ) {
            if (body1Text != null) {
                Text(
                    text = body1Text,
                    style = body1TextStyle
                )
            }

            if (isOpenInNew){
                MySpacerRow(width = 8.dp)

                DisplayIcon(icon = MyIcons.openInNew)
            }

            if (body2Text != null) {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = body2Text,
                    style = body2TextStyle
                )
            }
        }
    }
}