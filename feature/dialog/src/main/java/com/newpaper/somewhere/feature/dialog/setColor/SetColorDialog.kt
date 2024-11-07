package com.newpaper.somewhere.feature.dialog.setColor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.dateColorList
import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.feature.dialog.ButtonLayout
import com.newpaper.somewhere.feature.dialog.CancelDialogButton
import com.newpaper.somewhere.feature.dialog.DialogButtons
import com.newpaper.somewhere.feature.dialog.OkDialogButton
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@Composable
fun SetColorDialog(
    initialColor: MyColor,

    onDismissRequest: () -> Unit,
    onOkClick: (newColor: MyColor) -> Unit
){
    var dateColor by remember { mutableStateOf(initialColor) }

    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,

        titleText = stringResource(id = R.string.set_color),
        bodyContent = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                items(dateColorList) {
                    OneColor(
                        myColor = it,
                        isSelected = it == dateColor,
                        onClick = { dateColor = it }
                    )
                }
            }
        },
        buttonContent = {
            DialogButtons(
                buttonLayout = ButtonLayout.HORIZONTAL,
                negativeButtonContent = {
                    //cancel button
                    CancelDialogButton(
                        onClick = onDismissRequest,
                        modifier = it
                    )
                },
                positiveButtonContent = {
                    //ok button
                    OkDialogButton(
                        onClick = { onOkClick(dateColor) },
                        modifier = it
                    )
                }
            )
        }
    )
}

@Composable
private fun OneColor(
    myColor: MyColor,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .size(70.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(Color(myColor.color))
//            .fillMaxSize()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected)
            DisplayIcon(icon = MyIcons.selectedColor, color = Color(myColor.onColor))
    }
}





















@Composable
@PreviewLightDark
private fun Preview_SetColorDialog(){
    SomewhereTheme {
        MyScaffold {
            SetColorDialog(
                initialColor = MyColor(),
                onDismissRequest = {},
                onOkClick = {}
            )
        }
    }
}