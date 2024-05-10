package com.newpaper.somewhere.core.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun InternetUnavailableIconWithText(){
    DisplayIcon(icon = MyIcons.internetUnavailable)
    MySpacerColumn(height = 6.dp)
    Text(
        text = stringResource(id = R.string.internet_unavailable_check_connection),
        textAlign = TextAlign.Center
    )
}