package com.newpaper.somewhere.feature.trip.image

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.MyScaffold
import com.newpaper.somewhere.core.designsystem.component.topAppBars.ImageTopAppBar
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.feature.trip.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageRoute(
    imageUerId: String,
    internetEnabled: Boolean,
    imagePathList: List<String>,
    initialImageIndex: Int,

    navigateUp: () -> Unit,
    downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit,
    saveImageToExternalStorage: (imageFileName: String) -> Boolean,

    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val downloadCompleteText = stringResource(id = R.string.toast_download_complete)
    val downloadErrorText = stringResource(id = R.string.toast_download_error)

    val onClickBack = {
        if (!systemUiController.isSystemBarsVisible)
            systemUiController.isSystemBarsVisible = true
        navigateUp()
    }

    BackHandler {
        onClickBack()
    }

    var showImageOnly by rememberSaveable { mutableStateOf(false) }

    val pageState = rememberPagerState(
        initialPage = initialImageIndex,
        pageCount = { imagePathList.size }
    )

    ImageScreen(
        imageUerId = imageUerId,
        internetEnabled = internetEnabled,
        pageState = pageState,
        imagePathList = imagePathList,
        showImageOnly = showImageOnly,
        onClickBack = onClickBack,
        onClickImage = {
            showImageOnly = !showImageOnly
            systemUiController.isSystemBarsVisible = !showImageOnly
        },
        onClickDownloadImage = {
            val saveResult = saveImageToExternalStorage(imagePathList[pageState.currentPage])

            if (saveResult) Toast.makeText(context, downloadCompleteText, Toast.LENGTH_SHORT).show()
            else            Toast.makeText(context, downloadErrorText, Toast.LENGTH_SHORT).show()
        },
        downloadImage = downloadImage,
        modifier = modifier
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageScreen(
    imageUerId: String,
    internetEnabled: Boolean,

    pageState: PagerState,
    imagePathList: List<String>,
    showImageOnly: Boolean,

    onClickBack: () -> Unit,
    onClickImage: () -> Unit,
    onClickDownloadImage: (imageFileName: String) -> Unit,
    downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        modifier = modifier,
        topBar = {
            ImageTopAppBar(
                visible = !showImageOnly,
                title = "${pageState.currentPage + 1} / ${imagePathList.size}",
                navigationIconOnClick = { onClickBack() },
                actionIcon1Onclick = { onClickDownloadImage(imagePathList[pageState.currentPage]) }
            )
        },
        bottomBar = {

        }
    ) { _ ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CustomColor.imageBackground),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pageState,
                beyondBoundsPageCount = 3,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                pageContent = {

                    //each image(page) have zoom state
                    val zoomState = rememberZoomState(maxScale = 6f)

                    LaunchedEffect(pageState.currentPage) {
                        zoomState.reset()
                    }

                    //why using this? BoxWithConstraints
                    BoxWithConstraints(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onClickImage
                            )
                    ) {

                        val a = this

                        ImageFromFile(
                            internetEnabled = internetEnabled,
                            imageUserId = imageUerId,
                            imagePath = imagePathList[it],
                            contentDescription = stringResource(id = R.string.image),
                            downloadImage =  downloadImage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .zoomable(
                                    zoomState = zoomState,
//                                    onTap = { onClickImage() }
                                ),
                            contentScale = ContentScale.Fit,
                            isImageScreen = true,
//                            onClick = onClickImage
                        )
                    }
                }
            )
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
@PreviewLightDark
private fun ImageScreenPreview(){
    SomewhereTheme {
        MyScaffold {
            ImageScreen(
                imageUerId = "",
                internetEnabled = true,
                pageState = rememberPagerState { 1 },
                imagePathList = listOf("", ""),
                showImageOnly = false,
                onClickBack = { },
                onClickImage = { },
                onClickDownloadImage = { },
                downloadImage = {_,_,_ -> }
            )
        }
    }
}



