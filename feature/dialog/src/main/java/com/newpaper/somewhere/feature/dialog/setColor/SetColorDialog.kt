package com.newpaper.somewhere.feature.dialog.setColor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.blackInt
import com.newpaper.somewhere.core.model.data.MyColor
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.DialogButton
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

private val dateColorList = listOf(
    MyColor(0xFF493cfa.toInt()),
    MyColor(0xFF7168e8.toInt()),
    MyColor(0xFF000000.toInt()),
    MyColor(0xFFffffff.toInt(), blackInt),

    MyColor(0xFF7f7f7f.toInt(), blackInt),
    MyColor(0xFFc3c3c3.toInt(), blackInt),
    MyColor(0xFF880015.toInt()),
    MyColor(0xFFb97a57.toInt()),

    MyColor(0xFFed1c24.toInt()),
    MyColor(0xFFffaec9.toInt(), blackInt),
    MyColor(0xFFff7f27.toInt(), blackInt),
    MyColor(0xFFffc90e.toInt(), blackInt),

    MyColor(0xFFfff200.toInt(), blackInt),
    MyColor(0xFFefe4b0.toInt(), blackInt),
    MyColor(0xFF22b14c.toInt(), blackInt),
    MyColor(0xFFb5e61d.toInt(), blackInt),

    MyColor(0xFF00a2e8.toInt(), blackInt),
    MyColor(0xFF99d9ea.toInt(), blackInt),
    MyColor(0xFF3f48cc.toInt()),
    MyColor(0xFF7092be.toInt(), blackInt),

    MyColor(0xFFa349a4.toInt()),
    MyColor(0xFFc8bfe7.toInt(), blackInt),
    MyColor(0xFF732bf5.toInt()),
    MyColor(0xFF3a083e.toInt()),

    MyColor(0xFF75fa8d.toInt(), blackInt),
    MyColor(0xFF73fbfd.toInt(), blackInt),
    MyColor(0xFF3a0603.toInt()),
    MyColor(0xFF183e0c.toInt()),

    MyColor(0xFF817f26.toInt()),
    MyColor(0xFF0023f5.toInt()),
    MyColor(0xFF75163f.toInt()),
    MyColor(0xFF7f82bb.toInt(), blackInt),
)

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
            Row {
                //cancel button
                DialogButton(
                    text = stringResource(id = R.string.button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.button_ok),
                    onClick = {
                        onOkClick(dateColor)
                    }
                )
            }
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