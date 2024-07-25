package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.button.CopyAppPlayStoreLinkButton
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.ui.R

@Composable
fun ShareAppCard(
    onClickCopyPlayStoreLink: () -> Unit,
    modifier: Modifier = Modifier
){
    MyCard(
        modifier = Modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(16.dp)
        ) {
            CopyAppPlayStoreLinkButton(
                onClick = onClickCopyPlayStoreLink
            )

            Text(
                text = stringResource(id = R.string.or),
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            MySpacerColumn(height = 12.dp)
            //qr code
            ImageFromDrawable(
                imageDrawable = R.drawable.play_store_qr,
                contentDescription = "",
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

        }
    }
}

























@Composable
@PreviewLightDark
private fun ShareAppCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            ShareAppCard(
                onClickCopyPlayStoreLink = { }
            )
        }
    }
}