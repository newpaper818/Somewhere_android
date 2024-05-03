package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    currentVersionName: String,
    isLatestVersion: Boolean?,
    onClickUpdate: () -> Unit
){

    val text =
        when (isLatestVersion){
            true -> currentVersionName + " " + stringResource(id = R.string.latest)
            false -> currentVersionName + " " + stringResource(id = R.string.old_version)
            else -> currentVersionName
        }


    MyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(16.dp, 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.version),
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                MySpacerColumn(height = 8.dp)

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (isLatestVersion == false) {
                Spacer(modifier = Modifier.weight(1f))
                
                MySpacerRow(width = 6.dp)

                UpdateButton(
                    onClick = onClickUpdate
                )
            }
        }
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
                currentVersionName = "1.6.0",
                isLatestVersion = true,
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
                currentVersionName = "1.6.0",
                isLatestVersion = false,
                onClickUpdate = { }
            )
        }
    }
}