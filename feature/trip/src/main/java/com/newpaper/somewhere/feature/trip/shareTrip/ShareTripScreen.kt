package com.newpaper.somewhere.feature.trip.shareTrip

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.ImageFromUri
import com.newpaper.somewhere.core.designsystem.component.button.SaveAsImageButton
import com.newpaper.somewhere.core.designsystem.component.button.ShareMoreButton
import com.newpaper.somewhere.core.designsystem.component.button.ShareToInstagramStoryButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.utils.convert.getEndDateText
import com.newpaper.somewhere.core.utils.convert.getStartDateText
import com.newpaper.somewhere.feature.trip.R

@Composable
fun ShareTripRoute(
    spacerValue: Dp,
    dateTimeFormat: DateTimeFormat,

    trip: Trip,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    shareTripViewModel: ShareTripViewModel = hiltViewModel()
){
    val shareTripUiState by shareTripViewModel.shareTripUiState.collectAsState()

    val downloadCompleteText = stringResource(id = R.string.toast_download_complete)
    val downloadErrorText = stringResource(id = R.string.toast_download_error)

    val context = LocalContext.current

    val instagramLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> }

    val tripImagePath = trip.imagePathList.getOrNull(0)

    // make background, sticker asset
    LaunchedEffect(Unit) {
        val tripTitle = trip.titleText
        val tripStartDate = trip.getStartDateText(dateTimeFormat.copy(includeDayOfWeek = false), true)
        val tripEndDate = trip.getEndDateText(dateTimeFormat.copy(includeDayOfWeek = false), true)

        shareTripViewModel.createAsset(context, tripImagePath, tripTitle, tripStartDate, tripEndDate)
    }

    ShareTripScreen(
        spacerValue = spacerValue,
        tripImagePath = tripImagePath,
        stickerAssetUri = shareTripUiState.stickerAssetUri,
        onClickShareToInstagramStory = {
            shareTripViewModel.onClickShareToInstagramStory(context, instagramLauncher)
        },
        onClickSaveAsImage = {
            if (shareTripViewModel.onClickSaveAsImage())
                Toast.makeText(context, downloadCompleteText, Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, downloadErrorText, Toast.LENGTH_SHORT).show()
        },
        onClickShareMore = {
            shareTripViewModel.onClickShareMore(context)
        },
        navigateUp = navigateUp,
        modifier = modifier

    )
}

@Composable
private fun ShareTripScreen(
    spacerValue: Dp,

    tripImagePath: String?,
    stickerAssetUri: Uri?,

    onClickShareToInstagramStory: () -> Unit,
    onClickSaveAsImage: () -> Unit,
    onClickShareMore: () -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        modifier = modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
            .imePadding(),
        snackbarHost = {

        },
        topBar = {
            SomewhereTopAppBar(
                startPadding = spacerValue,
                title = stringResource(id = R.string.share_trip),
                navigationIcon = TopAppBarIcon.close,
                onClickNavigationIcon = { navigateUp() }
            )
        }
    ) { paddingValues ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            //preview
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                //background
                if (tripImagePath != null){
                    ImageFromFile(
                        internetEnabled = false,
                        imageUserId = "",
                        imagePath = tripImagePath,
                        contentDescription = "image",
                        downloadImage = {_,_,_ -> },
                        modifier = Modifier.fillMaxSize().blur(8.dp),
                    )
                }

                //sticker
                ImageFromUri(
                    imageUri = stickerAssetUri,
                    contentDescription = "trip sticker image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(48.dp, 64.dp)
                )

                //gradient
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(MaterialTheme.colorScheme.background, Color.Transparent)
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf( Color.Transparent, MaterialTheme.colorScheme.background)
                                    )
                                )
                        )
                    }
                }
            }


            MySpacerColumn(12.dp)

            //buttons
            // save to image / share to IG story / other
            ShareButtons(
                enabled = stickerAssetUri != null,
                onClickShareToInstagramStory,
                onClickSaveAsImage,
                onClickShareMore
            )

            MySpacerColumn(16.dp)
        }
    }
}




@Composable
private fun ShareButtons(
    enabled: Boolean,
    onClickShareToInstagramStory: () -> Unit,
    onClickSaveAsImage: () -> Unit,
    onClickShareMore: () -> Unit
){
    Row(
        modifier = Modifier.widthIn(max = 400.dp)
    ) {
        MySpacerRow(16.dp)

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ){
            ShareToInstagramStoryButton(
                enabled = enabled,
                onClick = onClickShareToInstagramStory
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            SaveAsImageButton(
                enabled = enabled,
                onClick = onClickSaveAsImage
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            ShareMoreButton(
                enabled = enabled,
                onClick = onClickShareMore
            )
        }

        MySpacerRow(16.dp)
    }
}















