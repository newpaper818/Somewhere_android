package com.example.somewhere.ui.tripScreenUtils.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.model.Date
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.theme.MyColor
import com.example.somewhere.ui.theme.myColorLists

@Composable
fun SetColorDialog(
    currentDate: Date,

    onDismissRequest: () -> Unit,
    onOkClick: (newMyColor: MyColor) -> Unit
){
    var dateColor by remember { mutableStateOf(currentDate.color) }

    MyDialog(
        onDismissRequest = onDismissRequest,
        setMaxHeight = true,

        titleText = stringResource(id = R.string.dialog_title_set_color),
        bodyContent = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .heightIn(min = 0.dp)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                items(myColorLists) {
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
                    text = stringResource(id = R.string.dialog_button_cancel),
                    onClick = onDismissRequest
                )

                MySpacerRow(width = 16.dp)

                //ok button
                DialogButton(
                    text = stringResource(id = R.string.dialog_button_ok),
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