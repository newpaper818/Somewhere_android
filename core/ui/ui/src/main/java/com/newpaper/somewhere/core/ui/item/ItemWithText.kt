package com.newpaper.somewhere.core.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

@Composable
fun ItemWithText(
    isSelected: Boolean = false,
    text: String?= null,
    subText: String? = null,

    isOpenInNew: Boolean = false,
    onItemClick: (() -> Unit)? = null
){
    val textStyle = MaterialTheme.typography.bodyLarge
    val subTextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)


    ClickableBox(
        containerColor = if (isSelected) MaterialTheme.colorScheme.outlineVariant
                            else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth(),
//            .height(60.dp),
        enabled = onItemClick != null,
        onClick = onItemClick ?: { }
    ) {
        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(16.dp, 0.dp)
            ) {
                if (text != null) {
                    Text(
                        text = text,
                        style = textStyle
                    )
                }

                if (isOpenInNew) {
                    MySpacerRow(width = 8.dp)

                    DisplayIcon(icon = MyIcons.openInNew)
                }

                if (subText != null) {
                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = subText,
                        style = subTextStyle,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp)
                    )
                }
            }
        }
    }
}

















@Composable
@PreviewLightDark
private fun Preview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(250.dp)
        ) {
            ListGroupCard {
                ItemWithText(
                    text = "item text"
                )

                ItemDivider()

                ItemWithText(
                    text = "item text",
                    subText = "subtext"
                )

                ItemDivider()

                ItemWithText(
                    isSelected = true,
                    text = "selected item",
                    subText = "subtext"
                )

                ItemDivider()

                ItemWithText(
                    text = "item",
                    subText = "subtext",
                    isOpenInNew = true
                )
            }
        }
    }
}