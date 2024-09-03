package com.newpaper.somewhere.feature.trip.inviteFriend.component

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.button.InviteFriendWithEmailButton
import com.newpaper.somewhere.core.designsystem.component.button.SettingsButton
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MyPlainTooltipBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.utils.CAMERA_PERMISSION_ARRAY
import com.newpaper.somewhere.core.utils.checkCameraPermission
import com.newpaper.somewhere.feature.trip.R
import kotlinx.coroutines.delay
import org.publicvalue.multiplatform.qrcode.CodeType
import org.publicvalue.multiplatform.qrcode.Scanner

@Composable
internal fun QrCodePage(
    internetEnabled: Boolean,
    searchFriendAvailable: Boolean,
    onScanned: (String) -> Unit,
    onClickInviteWithEmailButton: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        QrCodeScanner(
            internetEnabled = internetEnabled,
            searchFriendAvailable = searchFriendAvailable,
            onScanned = onScanned
        )

        MySpacerColumn(height = 16.dp)

        Text(
            text = stringResource(id = R.string.scan_friends_qr),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center
        )

        MySpacerColumn(height = 24.dp)

        InviteFriendWithEmailButton(
            onClick = onClickInviteWithEmailButton
        )
    }
}

@Composable
private fun QrCodeScanner(
    internetEnabled: Boolean,
    searchFriendAvailable: Boolean,
    onScanned: (String) -> Unit
){
    val context = LocalContext.current

    var cameraPermissionGranted by rememberSaveable {
        mutableStateOf(
            checkCameraPermission(context)
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ _ ->
        //after end permission dialog
        cameraPermissionGranted = checkCameraPermission(context)
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted){
            cameraPermissionLauncher.launch(CAMERA_PERMISSION_ARRAY)
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted){
            while (true) {
                cameraPermissionGranted = checkCameraPermission(context)
                if (cameraPermissionGranted)
                    break
                delay(3000)
            }
        }
    }

    Box {
        AnimatedVisibility(
            visible = cameraPermissionGranted,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            QrScan(
                internetEnabled = internetEnabled,
                searchFriendAvailable = searchFriendAvailable,
                onScanned = {
                    onScanned(it)
                }
            )
        }

        AnimatedVisibility(
            visible = !cameraPermissionGranted,
            enter = fadeIn(tween(500)),
            exit = fadeOut(tween(500))
        ) {
            CameraPermissionNotGrantedCard(
                onClickSettingsButton = {
                    //go to app settings
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }
            )
        }
    }
}





@Composable
private fun QrScan(
    internetEnabled: Boolean,
    searchFriendAvailable: Boolean,
    onScanned: (String) -> Unit
){
    Box(
        modifier = Modifier.size(304.dp),
        contentAlignment = Alignment.Center
    ) {
        Scanner(
            modifier = Modifier
                .size(300.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            onScanned = {
                if (internetEnabled)
                    onScanned(it)
                false
            },
            types = listOf(CodeType.QR)
        )

        //border line
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(MaterialTheme.shapes.extraLarge)
                .border(
                    width = 5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )

        var flash by rememberSaveable {
            mutableStateOf(false)
        }

//        val context = LocalContext.current
//        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//        val cameraId = cameraManager.cameraIdList[0]
//        cameraManager.setTorchMode(cameraId, true)

        
        //flash light button
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.BottomEnd
//        ){
//            FlashLightButton(
//                isFlashLightTurnedOn = flash,
//                onClick = { flash = !flash },
//                modifier = Modifier.padding(20.dp)
//            )
//        }

        //scan unavailable
        AnimatedVisibility(
            visible = !searchFriendAvailable && internetEnabled,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            ScanUnavailableBox()
        }

        //no internet text
        AnimatedVisibility(
            visible = !internetEnabled,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            InternetUnavailableBox()
        }
    }
}

@Composable
private fun FlashLightButton(
    isFlashLightTurnedOn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    MyCard(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f)),
        modifier = modifier
    ) {
        MyPlainTooltipBox(
            tooltipText = if (isFlashLightTurnedOn) stringResource(id = MyIcons.flashOn.descriptionTextId)
            else stringResource(id = MyIcons.flashOff.descriptionTextId)
        ) {
            IconButton(
                onClick = onClick
            ) {
                DisplayIcon(
                    icon = if (isFlashLightTurnedOn) MyIcons.flashOn
                        else MyIcons.flashOff
                )
            }
        }
    }
}

@Composable
private fun ScanUnavailableBox(

){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color.Black.copy(alpha = 0.6f))
    )
}

@Composable
private fun InternetUnavailableBox(

){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(Color.Black.copy(alpha = 0.6f))
        ,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            DisplayIcon(icon = MyIcons.internetUnavailableWhite)

            MySpacerColumn(height = 12.dp)

            Text(
                text = stringResource(id = R.string.internet_unavailable_),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )

            MySpacerColumn(height = 10.dp)

            Text(
                text = stringResource(id = R.string.check_internet_connection),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
        }
    }
}

@Composable
private fun CameraPermissionNotGrantedCard(
    onClickSettingsButton: () -> Unit
){
    MyCard(
        modifier = Modifier.size(304.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DisplayIcon(icon = MyIcons.qrCode)

                MySpacerColumn(height = 12.dp)

                Text(
                    text = stringResource(id = R.string.camera_permission_denied),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                MySpacerColumn(height = 8.dp)

                Text(
                    text = stringResource(id = R.string.please_allow_camera_permissions),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

                MySpacerColumn(height = 16.dp)

                SettingsButton(
                    onClick = onClickSettingsButton
                )
            }
        }
    }
}