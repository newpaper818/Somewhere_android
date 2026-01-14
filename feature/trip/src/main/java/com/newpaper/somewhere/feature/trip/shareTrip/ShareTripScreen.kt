package com.newpaper.somewhere.feature.trip.shareTrip

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.ImageFromUri
import com.newpaper.somewhere.core.designsystem.component.button.SaveAsImageButton
import com.newpaper.somewhere.core.designsystem.component.button.ShareMoreButton
import com.newpaper.somewhere.core.designsystem.component.button.ShareToInstagramStoryButton
import com.newpaper.somewhere.core.designsystem.component.topAppBars.SomewhereTopAppBar
import com.newpaper.somewhere.core.designsystem.component.utils.DotsIndicator
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.TopAppBarIcon
import com.newpaper.somewhere.core.model.data.DateTimeFormat
import com.newpaper.somewhere.core.model.tripData.Trip
import com.newpaper.somewhere.core.ui.item.ItemWithSwitch
import com.newpaper.somewhere.core.ui.item.ListGroupCard
import com.newpaper.somewhere.core.utils.convert.getEndDateText
import com.newpaper.somewhere.core.utils.convert.getStartDateText
import com.newpaper.somewhere.feature.trip.R
import kotlin.math.abs

@Composable
fun ShareTripRoute(
    isDarkAppTheme: Boolean,
    spacerValue: Dp,
    dateTimeFormat: DateTimeFormat,

    trip: Trip,
    imageIndex: Int,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    shareTripViewModel: ShareTripViewModel = hiltViewModel()
){
    val shareTripUiState by shareTripViewModel.shareTripUiState.collectAsStateWithLifecycle()

    val downloadCompleteText = stringResource(id = R.string.toast_download_complete)
    val downloadErrorText = stringResource(id = R.string.toast_download_error)

    val context = LocalContext.current

    val instagramLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result -> }

    val tripImagePath = trip.imagePathList.getOrNull(imageIndex)

    // make background, sticker asset
    LaunchedEffect(Unit) {
        val tripTitle = trip.titleText
        val tripStartDate = trip.getStartDateText(dateTimeFormat.copy(includeDayOfWeek = false), true)
        val tripEndDate = trip.getEndDateText(dateTimeFormat.copy(includeDayOfWeek = false), true)

        shareTripViewModel.createAsset(context, tripImagePath, tripTitle, tripStartDate, tripEndDate)
    }

    ShareTripScreen(
        isDarkAppTheme = isDarkAppTheme,
        spacerValue = spacerValue,
        useBackground = shareTripUiState.useBackground,

        tripImagePath = tripImagePath,
        backgroundAssetUris = shareTripUiState.backgroundAssetUris,
        stickerAssetUris = shareTripUiState.stickerAssetUris,
        onUseBackgroundChanged = shareTripViewModel::setUseBackground,

        onClickShareToInstagramStory = { backgroundAssetUri, stickerAssetUri ->
            shareTripViewModel.setSelectedAssets(backgroundAssetUri, stickerAssetUri)
            shareTripViewModel.onClickShareToInstagramStory(context, instagramLauncher)
        },
        onClickSaveAsImage = { backgroundAssetUri, stickerAssetUri ->
            shareTripViewModel.setSelectedAssets(backgroundAssetUri, stickerAssetUri)

            if (shareTripViewModel.onClickSaveAsImage())
                Toast.makeText(context, downloadCompleteText, Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, downloadErrorText, Toast.LENGTH_SHORT).show()
        },
        onClickShareMore = { backgroundAssetUri, stickerAssetUri ->
            shareTripViewModel.setSelectedAssets(backgroundAssetUri, stickerAssetUri)
            shareTripViewModel.onClickShareMore(context)
        },

        navigateUp = navigateUp,
        modifier = modifier

    )
}

@Composable
private fun ShareTripScreen(
    isDarkAppTheme: Boolean,
    spacerValue: Dp,

    useBackground: Boolean,

    tripImagePath: String?,
    backgroundAssetUris: List<Uri?>,
    stickerAssetUris: List<Uri?>,

    onUseBackgroundChanged: (useBackground: Boolean) -> Unit,

    onClickShareToInstagramStory: (backgroundAssetUri: Uri?, stickerAssetUri: Uri?) -> Unit,
    onClickSaveAsImage: (backgroundAssetUri: Uri?, stickerAssetUri: Uri?) -> Unit,
    onClickShareMore: (backgroundAssetUri: Uri?, stickerAssetUri: Uri?) -> Unit,

    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
){
    var boxWidth by rememberSaveable { mutableIntStateOf(0) }
    val density = LocalDensity.current.density

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

        val pageState = rememberPagerState { stickerAssetUris.size }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            //preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned {
                        boxWidth = it.size.width
                    },
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )

                //background
                androidx.compose.animation.AnimatedVisibility(
                    visible = tripImagePath != null && useBackground,
                    enter = fadeIn(tween(300)),
                    exit = fadeOut(tween(300))
                ) {
                    ImageFromFile(
                        internetEnabled = false,
                        imageUserId = "",
                        imagePath = tripImagePath!!,
                        contentDescription = stringResource(R.string.background_image),
                        downloadImage = { _, _, _ -> },
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(
                                getBlur(
                                    pageState.currentPageOffsetFraction,
                                    pageState.currentPage
                                ).dp
                            ),
                    )
                }


                //stickers
                HorizontalPager(
                    state = pageState,
                    pageSize = PageSize.Fixed((boxWidth / density).toInt().dp - 100.dp),
                    contentPadding = PaddingValues(50.dp, 0.dp),
                    beyondViewportPageCount = 2,
                    pageContent = { it ->
                        ImageFromUri(
                            imageUri = stickerAssetUris[it],
                            contentDescription = stringResource(R.string.sticker_image),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp, 64.dp)
                        )
                    }
                )




                //gradient
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.background,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        ){
                            DotsIndicator(isDarkAppTheme, pageState.pageCount, pageState.currentPage)
                        }
                    }
                }
            }

            MySpacerColumn(12.dp)

            //use background switch toggle
            if (tripImagePath != null) {
                ListGroupCard(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .widthIn(max = 300.dp)
                ) {
                    ItemWithSwitch(
                        text = stringResource(id = R.string.use_background),
                        checked = useBackground,
                        onCheckedChange = onUseBackgroundChanged
                    )
                }
            }


            MySpacerColumn(12.dp)

            //buttons
            // save to image / share to IG story / other
            ShareButtons(
                enabled = stickerAssetUris.filterNotNull().isNotEmpty(),
                onClickShareToInstagramStory = {
                    onClickShareToInstagramStory(
                        if (useBackground) backgroundAssetUris[pageState.currentPage] else null,
                        stickerAssetUris[pageState.currentPage])
                },
                onClickSaveAsImage = {
                    onClickSaveAsImage(
                        if (useBackground) backgroundAssetUris[pageState.currentPage] else null,
                        stickerAssetUris[pageState.currentPage])
                },
                onClickShareMore = {
                    onClickShareMore(
                        if (useBackground) backgroundAssetUris[pageState.currentPage] else null,
                        stickerAssetUris[pageState.currentPage])
                }
            )

            MySpacerColumn(12.dp)
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


private fun getBlur(
    currentPageOffsetFraction: Float,
    currentPage: Int,
    blurRadius: Int = 8
): Float{
    val absPageOffsetFraction = abs(currentPageOffsetFraction)

    // y = -blurRadius * x(absPageOffsetFraction) + blurRadius
    return if(currentPage == 0){
        blurRadius - blurRadius * absPageOffsetFraction
    }
    // y = blurRadius * x(absPageOffsetFraction)
    else{
        absPageOffsetFraction * blurRadius
    }
}












