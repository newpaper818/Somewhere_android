package com.newpaper.somewhere.core.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.utils.listItemHeight


/**
 * use with [ListGroupCard]
 *
 * @param isSelected show gray background when true
 * @param icon icon at left side
 * @param titleText top small gray text
 * @param text main text
 * @param subText gray text at right side
 * @param iconRight icon at right side
 * @param onItemClick when click item
 */
@Composable
fun ItemWithText(
    isSelected: Boolean = false,
    icon: MyIcon? = null,
    titleText: String? = null,
    text: String?= null,
    subText: String? = null,

    iconRight: MyIcon? = null,

    onItemClick: (() -> Unit)? = null
){
    val titleTextStyle = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
    val textStyle = MaterialTheme.typography.bodyLarge
    val subTextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)


    ClickableBox(
        containerColor = if (isSelected) MaterialTheme.colorScheme.outlineVariant
                            else Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
        enabled = onItemClick != null,
        onClick = onItemClick ?: { }
    ) {
        Column {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = listItemHeight)
                    .padding(16.dp, 14.dp)
            ) {
                Column {
                    if (titleText != null) {
                        Text(
                            text = titleText,
                            style = titleTextStyle
                        )

                        MySpacerColumn(height = 8.dp)
                    }

                    Row {
                        if (icon != null) {
                            DisplayIcon(icon = icon)

                            MySpacerRow(width = 10.dp)
                        }

                        if (text != null) {
                            Text(
                                text = text,
                                style = textStyle
                            )

                            MySpacerRow(width = 8.dp)
                        }
                    }
                }

                if (subText != null || iconRight != null) {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (subText != null) {
                    Text(
                        text = subText,
                        style = subTextStyle,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp)
                    )
                }

                if (iconRight != null) {
                    MySpacerRow(width = 8.dp)

                    DisplayIcon(icon = iconRight)
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
                .width(300.dp)
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
                    iconRight = MyIcons.openInNew
                )

                ItemDivider()

                ItemWithText(
                    icon = MyIcons.time,
                    text = "item",
                    subText = "subtext",
                    iconRight = MyIcons.openInNew
                )

                ItemDivider()

                ItemWithText(
                    titleText = "title",
                    text = "item",
                    iconRight = MyIcons.sendEmail
                )

                ItemDivider()

                ItemWithText(
                    titleText = "title",
                    icon = MyIcons.time,
                    text = "item",
                    iconRight = MyIcons.openInNew
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PreviewDont(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(300.dp)
        ) {
            ItemWithText(
                titleText = "title",
                icon = MyIcons.time,
                text = "item",
                iconRight = MyIcons.openInNew
            )
        }
    }
}