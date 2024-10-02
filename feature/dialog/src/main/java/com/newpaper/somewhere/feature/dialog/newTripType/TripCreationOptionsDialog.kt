package com.newpaper.somewhere.feature.dialog.newTripType

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.feature.dialog.R
import com.newpaper.somewhere.feature.dialog.myDialog.MyDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripCreationOptionsDialog(
    onDismissRequest: () -> Unit,
    onClick: (onClickManual: Boolean) -> Unit
){
//    Dialog(onDismissRequest = { /*TODO*/ }) {
//
//    }
    BasicAlertDialog(
        modifier = Modifier
            .width(IntrinsicSize.Min)
//            .layout{ measurable, constraints ->
//                val placeable = measurable.measure(constraints);
//                layout(constraints.maxWidth, constraints.maxHeight){
//                    placeable.place(0, constraints.maxHeight - placeable.height - 40, 10f)
//                }
//            }
        ,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
//                    .width(500.dp)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TripCreationTypeButton(
                    icon = MyIcons.manual,
                    text = stringResource(id = R.string.enter_your_own),
                    onClick = { onClick(true) },
//                    modifier = Modifier.weight(1f)
                )

                MySpacerColumn(height = 16.dp)

                TripCreationTypeButton(
                    icon = MyIcons.ai,
                    text = stringResource(id = R.string.create_with_AI),
                    onClick = { onClick(false) },
//                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}



@Composable
private fun TripCreationTypeButton(
    icon: MyIcon,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    ClickableBox(
        modifier = modifier.height(76.dp).fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceBright,
        contentAlignment = Alignment.Center,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayIcon(icon = icon)

            MySpacerRow(width = 8.dp)

            Text(
                text = text,
                textAlign = TextAlign.Center
            )
        }
    }
}