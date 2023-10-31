package com.newpaper.somewhere.ui.tripScreens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.newpaper.somewhere.ui.commonScreenUtils.ImageTopAppBar
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.commonScreenUtils.SomewhereTopAppBar
import com.newpaper.somewhere.ui.navigation.NavigationDestination
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.n10
import com.newpaper.somewhere.ui.tripScreenUtils.cards.DisplayImage

object ImageDestination : NavigationDestination {
    override val route = "image"
    override var title = ""
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageScreen(
    imagePathList: List<String>,
    initialImageIndex: Int,

    navigateUp: () -> Unit,
){


    val pageState = rememberPagerState(
        initialPage = initialImageIndex,
        pageCount = { imagePathList.size }
    )

    Scaffold(
        topBar = {
            ImageTopAppBar(
                title = "${pageState.currentPage + 1}/${imagePathList.size}",
                navigationIconOnClick = {
                    navigateUp()
                },

                //TODO download image
//                actionIcon1 = MyIcons.download,
//                actionIcon1Onclick = {
//
//                }
            )
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


                    var scale by remember { mutableFloatStateOf(1f) }
                    var offset by remember { mutableStateOf(Offset.Zero) }

                    LaunchedEffect(key1 = pageState.currentPage) {
                        scale = 1f
                        offset = Offset.Zero
                    }

                    BoxWithConstraints(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        val state = rememberTransformableState { zoomChange, panChange, _ ->
                            scale = (scale * zoomChange).coerceIn(1f, 5f)

                            val extraWidth = (scale - 1) * constraints.maxWidth
                            val extraHeight = (scale - 1) * constraints.maxHeight

                            val maxX = extraWidth / 2
                            val maxY = extraHeight / 2

                            offset = Offset(
                                x = (offset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                                y = (offset.y + scale * panChange.y).coerceIn(-maxY, maxY)
                            )

                            offset += panChange
                        }

                        DisplayImage(
                            imagePath = imagePathList[it],
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = offset.x
                                    translationY = offset.y
                                }
                                .transformable(state)
                        )
                    }
                }
            )
        }
    }
}