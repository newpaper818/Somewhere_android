package com.newpaper.somewhere.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.ui.designsystem.R
import java.io.File

@Composable
fun ImageFromDrawable(
    @DrawableRes imageDrawable: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
){
    Image(
        painter = painterResource(id = imageDrawable),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}

@Composable
fun ImageFromUrl(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
){
    val context = LocalContext.current
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }

    if (isLoading){
        OnLoadingImage()
    }

    if (isError){
        OnErrorImage()
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .crossfade(300)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        onLoading = {
            isLoading = true
        },
        onSuccess = {
            isLoading = false
            isError = false
        },
        onError = {
            isLoading = false
            isError = true
        }
    )
}

@Composable
fun ImageFromFile(
    internetEnabled: Boolean,
    imagePath: String,
    contentDescription: String,
    downloadImage: (imagePath: String) -> Boolean,

    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    isImageScreen: Boolean = false,
    onClick: () -> Unit = { }
){
    val context = LocalContext.current
    val imageFile = File(context.filesDir, imagePath)

    var imageFileExit by rememberSaveable { mutableStateOf(imageFile.exists()) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }


    if (isLoading){
        OnLoadingImage(
            isImageScreen = isImageScreen
        )
    }

    if (isError){
        OnErrorImage(
            isImageScreen = isImageScreen,
            onClick = onClick
        )
    }

    if (imageFileExit) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageFile)
                .crossfade(true)
                .crossfade(300)
                .build(),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
            onLoading = {
                isLoading = true
            },
            onSuccess = {
                isLoading = false
                isError = false
            },
            onError = {
                isLoading = false
                isError = true
            }
        )
    }
    else {
        LaunchedEffect(internetEnabled) {
            if (internetEnabled){
                isLoading = true
                isError = false

                val downloadImageResult = downloadImage(imagePath)


                if (downloadImageResult) {
                    imageFileExit = true
                }
                else {
                    isError = true
                    isLoading = false
                }
            }
            else{
                isLoading = false
                isError = true
            }
        }
    }
}
























@Composable
private fun OnLoadingImage(
    isImageScreen: Boolean = false
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .shimmerEffect(isImageScreen = isImageScreen)
    )
}

@Composable
private fun OnErrorImage(
    isImageScreen: Boolean = false,
    onClick: () -> Unit = { }
){
    var boxSize by remember{
        mutableStateOf(IntSize.Zero)
    }

    val minBoxLengthDp = with(LocalDensity.current){
        Integer.min(boxSize.height, boxSize.width).toDp()
    }

    val boxColor = if (isImageScreen) CustomColor.imageBackground
    else MaterialTheme.colorScheme.surfaceDim

    val onColor = if (isImageScreen) CustomColor.white
    else MaterialTheme.colorScheme.onSurface

    ClickableBox(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { boxSize = it },
        contentAlignment = Alignment.Center,
        containerColor = boxColor,
        enabled = isImageScreen,
        onClick = onClick
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayIcon(
                icon = MyIcons.imageLoadingError,
                color = onColor
            )

            if (minBoxLengthDp > 160.dp) {
                MySpacerColumn(height = 8.dp)

                Text(
                    text = stringResource(id = R.string.image_loading_error),
                    color = onColor
                )
            }
        }
    }
}



fun Modifier.shimmerEffect(
    isImageScreen: Boolean = false
): Modifier = composed {
    var size by remember{
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "infinite_transition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200)
        ),
        label = "shimmer_effect"
    )

    val colors = if (isImageScreen){
        listOf(
            CustomColor.imageBackground,
            Color(0xFF1A1A1A),
            Color(0xFF1A1A1A),
            CustomColor.imageBackground
        )
    }
    else {
        listOf(
            MaterialTheme.colorScheme.surfaceBright,
            MaterialTheme.colorScheme.surfaceTint,
            MaterialTheme.colorScheme.surfaceTint,
            MaterialTheme.colorScheme.surfaceBright
        )
    }


    background(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}