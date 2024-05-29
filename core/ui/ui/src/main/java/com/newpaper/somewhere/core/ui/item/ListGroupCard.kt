package com.newpaper.somewhere.core.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme

@Composable
fun ListGroupCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
    ) {
        if (title != null) {
            Row {
                MySpacerRow(width = 16.dp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            MySpacerColumn(height = 6.dp)
        }

        MyCard (modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun ItemDivider(
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp
){
    Row {
        MySpacerRow(width = startPadding)
        HorizontalDivider(modifier = Modifier.weight(1f))
        MySpacerRow(width = endPadding)
    }
}
























@Composable
@PreviewLightDark
private fun Preview_ListGroupCard(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            ListGroupCard(
                title = "list group card title"
            ) {
                ItemWithText(
                    text = "item text",
                )

                ItemDivider()

                ItemWithText(
                    text = "item text",
                    subText = "sub text"
                )

                ItemDivider()

                ItemWithText(
                    isSelected = true,
                    text = "item text",
                    subText = "sub text"
                )

                ItemDivider()

                ItemWithText(
                    text = "item text",
                    subText = "sub text"
                )
            }
        }
    }
}