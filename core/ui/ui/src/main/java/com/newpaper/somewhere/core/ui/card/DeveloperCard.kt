package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.R

@Composable
fun DeveloperCard(

){
    MyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp, 14.dp)
        ) {
            Text(
                text = stringResource(id = R.string.developer),
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            MySpacerColumn(height = 8.dp)

            Text(
                text = stringResource(id = R.string.developer_info),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}






















@Composable
@PreviewLightDark
private fun DeveloperCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DeveloperCard()
        }
    }
}