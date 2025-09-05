package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.UpdateButton
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun VersionCard(
    currentAppVersionName: String,
    isLatestAppVersion: Boolean?,
    onClickUpdate: () -> Unit,
    modifier: Modifier = Modifier
){
    val rowModifier = if (isLatestAppVersion == false) modifier.padding(start = 16.dp)
                        else modifier.padding(horizontal = 16.dp, vertical = 4.dp)

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = stringResource(id = R.string.version),
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        MySpacerRow(4.dp)
        Spacer(modifier = Modifier.weight(1f))

        VersionText(
            currentAppVersionName = currentAppVersionName,
            isLatestAppVersion = isLatestAppVersion
        )

        if (isLatestAppVersion == false) {
            MySpacerRow(width = 12.dp)

            UpdateButton(
                onClick = onClickUpdate
            )
        }
    }
}

@Composable
private fun VersionText(
    currentAppVersionName: String,
    isLatestAppVersion: Boolean?,
){
    val latestText = " " +
        when (isLatestAppVersion){
            true -> stringResource(id = R.string.latest)
            false -> stringResource(id = R.string.old_version)
            else -> ""
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics {
            contentDescription = "$currentAppVersionName$latestText"
        }
    ) {
        currentAppVersionName.forEach {
            Text(
                text = it.toString(),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = if (it.toString() == ".") Modifier
                            else Modifier.widthIn(min = 10.4.dp)
            )
        }

        Text(
            text = latestText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}






























@Composable
@PreviewLightDark
private fun VersionCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            VersionCard(
                currentAppVersionName = "1.6.0",
                isLatestAppVersion = true,
                onClickUpdate = { }
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun VersionCardOldPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            VersionCard(
                currentAppVersionName = "1.6.0",
                isLatestAppVersion = false,
                onClickUpdate = { }
            )
        }
    }
}