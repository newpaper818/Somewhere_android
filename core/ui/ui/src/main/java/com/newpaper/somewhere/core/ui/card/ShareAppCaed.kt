package com.newpaper.somewhere.core.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.lightspark.composeqr.DotShape
import com.lightspark.composeqr.QrCodeView
import com.newpaper.somewhere.core.designsystem.component.ImageFromDrawable
import com.newpaper.somewhere.core.designsystem.component.button.ShareAppButton
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.PLAY_STORE_URL

@Composable
fun ShareAppCard(
    onClickShareApp: () -> Unit,
    modifier: Modifier = Modifier
){
    MyCard(
        modifier = Modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(16.dp)
        ) {
            //share app
            ShareAppButton(
                onClick = onClickShareApp
            )

            //or
            Text(
                text = stringResource(id = R.string.or),
                style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.clearAndSetSemantics {  }
            )

            MySpacerColumn(height = 14.dp)

            //qr code
            AppQrCode()
        }
    }
}


@Composable
private fun AppQrCode(

){
    val density = LocalDensity.current

    var width by rememberSaveable {
        mutableIntStateOf(240)
    }

    val scanThisQrCodeToShare = stringResource(id = R.string.scan_this_qr_code)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .size(width = 240.dp, height = width.dp)
            .onSizeChanged {
                with(density) { width = it.width.toDp().value.toInt() }
            }
            .semantics {
                contentDescription = scanThisQrCodeToShare
            },
    ) {
        QrCodeView(
            data = PLAY_STORE_URL,
            modifier = Modifier.padding(12.dp),
            dotShape = DotShape.Circle
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                ImageFromDrawable(
                    imageDrawable = R.drawable.app_icon_fit,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(6.dp)
                        .fillMaxSize()
                )
            }
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
                onClickShareApp = { }
            )
        }
    }
}