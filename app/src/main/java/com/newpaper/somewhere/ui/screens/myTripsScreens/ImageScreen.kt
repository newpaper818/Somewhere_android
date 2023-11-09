package com.newpaper.somewhere.ui.screens.myTripsScreens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.ImageTopAppBar
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards.DisplayImage
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    imagePathList: List<String>,
    initialImageIndex: Int,

    navigateUp: () -> Unit,
){
    val systemUiController = rememberSystemUiController()

    val onBackClick = {
        if (!systemUiController.isSystemBarsVisible)
            systemUiController.isSystemBarsVisible = true
        navigateUp()
    }

    BackHandler {
        onBackClick()
    }

    var showImageOnly by rememberSaveable { mutableStateOf(false) }

    val pageState = rememberPagerState(
        initialPage = initialImageIndex,
        pageCount = { imagePathList.size }
    )
    Scaffold(
        topBar = {
            ImageTopAppBar(
                visible = !showImageOnly,
                title = "${pageState.currentPage + 1} / ${imagePathList.size}",
                navigationIconOnClick = { onBackClick() },

                //TODO download image
//                actionIcon1 = MyIcons.download,
//                actionIcon1Onclick = {
//
//                }
            )
        },
        bottomBar = {

        }
    ) { _ ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getColor(ColorType.IMAGE_BACKGROUND)),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pageState,
                beyondBoundsPageCount = 3,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                pageContent = {

                    val zoomState = rememberZoomState()

                    LaunchedEffect(pageState.currentPage) {
                        zoomState.reset()
                    }

                    BoxWithConstraints(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {


                        DisplayImage(
                            imagePath = imagePathList[it],
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .zoomable(
                                    zoomState = zoomState,
                                    onTap = {
                                        showImageOnly = !showImageOnly
                                        systemUiController.isSystemBarsVisible = !showImageOnly
                                    }
                                )
                        )
                    }
                }
            )
        }
    }
}