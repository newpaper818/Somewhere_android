package com.newpaper.somewhere.feature.dialog.memo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.HyperlinkedText
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun MemoDialog(
    memoText: String,
    onDismissRequest: () -> Unit,
) {

    MyDialog(
        onDismissRequest = onDismissRequest,
//        setMaxHeight = true,
        width = 600.dp,
//        maxHeight = 900.dp,
        setBodySpacer = false,
        closeIcon = true,

        titleText = stringResource(id = R.string.memo),
        bodyContent = {
            LazyColumn(
                contentPadding = PaddingValues(16.dp, 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 0.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceBright)

            ){
                item {
                    SelectionContainer {
                        HyperlinkedText(
                            text = memoText,
                            defaultTextStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        }
    )
}

























@Composable
@PreviewLightDark
private fun Preview_MemoDialog(){
    SomewhereTheme {
        MyScaffold {
            MemoDialog(
                memoText = "Modal date input color\n" +
                        "Color values are implemented through design tokens. For design, this means working with color values that correspond with tokens. For implementation, a color value will be a token that references a value." +
                        "Modal date input color",
                onDismissRequest = {}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_MemoDialog_long(){
    SomewhereTheme {
        MyScaffold {
            MemoDialog(
                memoText = "Modal date input color\n" +
                        "Color values are implemented through design tokens. For design, this means working with color values that correspond with tokens. For implementation, a color value will be a token that references a value." +
                        "Modal date input color\n" +
                        "Color values are implemented through design tokens. For design, this means working with color values that correspond with tokens. For implementation, a color value will be a token that references a value.Modal date input color\n" +
                        "Color values are implemented through design tokens. For design, this means working with color values that correspond with tokens. For implementation, a color value will be a token that references a value.Modal date input color\n" +
                        "Color values are implemented through design tokens. For design, this means working with color values that correspond with tokens. For implementation, a color value will be a token that references a value.Modal date input color\n" +
                        "Color values are implemented through design tokens. For design, this means working with color values that correspond with tokens. For implementation, a color value will be a token that references a value.",
                onDismissRequest = {}
            )
        }
    }
}