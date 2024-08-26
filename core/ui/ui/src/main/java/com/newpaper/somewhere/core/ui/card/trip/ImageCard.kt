package com.newpaper.somewhere.core.ui.card.trip

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.newpaper.somewhere.core.designsystem.component.ImageFromFile
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerColumn
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.designsystem.theme.n70
import com.newpaper.somewhere.core.designsystem.theme.n80
import com.newpaper.somewhere.core.ui.ui.R
import com.newpaper.somewhere.core.utils.SlideState
import com.newpaper.somewhere.core.utils.dragAndDropHorizontal
import kotlinx.coroutines.launch

private const val IMAGE_MAX_COUNT = 6

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    imageUserId: String,
    internetEnabled: Boolean,
    isEditMode: Boolean,
    imagePathList: List<String>,

    onClickImage: (imageIndex: Int) -> Unit,
    onAddImages: (List<String>) -> Unit,
    deleteImage: (String) -> Unit,
    isOverImage: (Boolean) -> Unit,

    reorderImageList: (currentIndex: Int, destinationIndex: Int) -> Unit,
    downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit,
    saveImageToInternalStorage: (index: Int, uri: Uri) -> String?,

    modifier: Modifier = Modifier
){
    val coroutineScope = rememberCoroutineScope()

    // 11 or over
    var isImageCountLimit by rememberSaveable { mutableStateOf(false) }

    val titleTextStyle1 = if (isImageCountLimit) MaterialTheme.typography.bodySmall.copy(color = CustomColor.outlineError)
                        else MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    val borderColor = if (isImageCountLimit) CustomColor.outlineError
                        else Color.Transparent

    val addImageTextStyle = if (!isImageCountLimit && imagePathList.size < IMAGE_MAX_COUNT) MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary)
                            else MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

    val modifier1 = if (isEditMode) modifier.sizeIn(maxHeight = 390.dp)
                    else modifier.sizeIn(maxWidth = 650.dp, maxHeight = 390.dp)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ){ uriList ->
        var addUriList = uriList

        if (imagePathList.size + uriList.size > IMAGE_MAX_COUNT && !isImageCountLimit){
            isImageCountLimit = true
            isOverImage(true)
        }

        if (uriList.size > 10){
            addUriList = addUriList.subList(0, 10)
        }

        val fileList: MutableList<String> = mutableListOf()

        coroutineScope.launch {
            //save to internal storage
            addUriList.forEachIndexed { index, uri ->
                val file = saveImageToInternalStorage(index, uri)
                if (file != null)
                    fileList.add(file)
            }
            onAddImages(fileList)
        }
    }

    AnimatedVisibility(
        visible = isEditMode || imagePathList.isNotEmpty(),
        enter = scaleIn(animationSpec = tween(300))
                + expandVertically(animationSpec = tween(300))
                + fadeIn(animationSpec = tween(300)),
        exit = scaleOut(animationSpec = tween(300))
                + shrinkVertically(animationSpec = tween(300))
                + fadeOut(animationSpec = tween(300))
    ) {

        Column(
            modifier = modifier
        ) {
            MyCard(
                modifier = modifier1
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            ) {
                Box {
                    Column {
                        //edit mode
                        AnimatedVisibility(
                            visible = isEditMode,
                            enter = fadeIn(animationSpec = tween(0, 0)),
                            exit = fadeOut(animationSpec = tween(500, 0))
                        ) {

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {

                                    //Images text
                                    Text(
                                        text = stringResource(
                                            id = R.string.image_card_title,
                                            imagePathList.size,
                                            IMAGE_MAX_COUNT
                                        ),
                                        style = titleTextStyle1,
                                        modifier = Modifier.padding(16.dp, 14.dp, 0.dp, 8.dp),
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    //add images text
                                    Text(
                                        text = stringResource(id = R.string.image_card_subtitle_add_images),
                                        style = addImageTextStyle,
                                        modifier = Modifier
                                            .clickable(
                                                enabled = !isImageCountLimit && imagePathList.size < IMAGE_MAX_COUNT,
                                                onClick = {
                                                    galleryLauncher.launch("image/*")
                                                }
                                            )
                                            .padding(16.dp, 14.dp, 16.dp, 8.dp)
                                    )
                                }

                                val slideStates = remember {
                                    mutableStateMapOf<String, SlideState>(
                                        *imagePathList.map { it to SlideState.NONE }.toTypedArray()
                                    )
                                }

                                //if no image
                                if (imagePathList.isEmpty()) {
                                    Text(
                                        text = stringResource(id = R.string.image_card_body_no_image),
                                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                        modifier = Modifier.padding(16.dp, 0.dp)
                                    )
                                }

                                //images
                                LazyRow(
                                    verticalAlignment = Alignment.CenterVertically,
                                    contentPadding = PaddingValues(16.dp, 0.dp, 0.dp, 0.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    items(imagePathList) { imagePath ->

                                        key(imagePathList) {
                                            val slideState =
                                                slideStates[imagePath] ?: SlideState.NONE

                                            ImageWithDeleteIcon(
                                                userId = imageUserId,
                                                internetEnabled = internetEnabled,
                                                imagePath = imagePath,
                                                imagePathList = imagePathList,
                                                onDeleteClick = {
                                                    deleteImage(imagePath)
                                                    if (imagePathList.size - 1 <= IMAGE_MAX_COUNT && isImageCountLimit) {
                                                        isImageCountLimit = false
                                                        isOverImage(false)
                                                    }
                                                },
                                                downloadImage = downloadImage,
                                                slideState = slideState,
                                                updateSlideState = { imageIndex, newSlideState ->
                                                    slideStates[imagePathList[imageIndex]] =
                                                        newSlideState
                                                },
                                                updateItemPosition = { currentIndex, destinationIndex ->
                                                    //on drag end
                                                    coroutineScope.launch {
                                                        //reorder list
                                                        reorderImageList(currentIndex, destinationIndex)

                                                        //all slideState to NONE
                                                        slideStates.putAll(imagePathList.associateWith { SlideState.NONE })
                                                    }
                                                }
                                            )
                                            MySpacerRow(width = 16.dp)
                                        }
                                    }
                                }

                                MySpacerColumn(height = 16.dp)
                            }
                        }
                    }

                    //not edit mode showing images
                    Column {
                        AnimatedVisibility(
                            visible = !isEditMode,
                            enter = expandVertically(tween(500)),
                            exit = shrinkVertically(tween(500))
                        ) {
                            val pageState = rememberPagerState { imagePathList.size }

                            ClickableBox(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    onClickImage(pageState.currentPage)
                                }
                            ) {
                                HorizontalPager(
                                    state = pageState,
                                    beyondBoundsPageCount = 3,
                                    pageContent = {
                                        ImageFromFile(
                                            internetEnabled = internetEnabled,
                                            imageUserId = imageUserId,
                                            imagePath = imagePathList[it],
                                            contentDescription = stringResource(id = R.string.image),
                                            downloadImage = downloadImage,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                )

                                if (imagePathList.size != 1)
                                    ImageIndicateDots(
                                        pageCount = imagePathList.size,
                                        currentPage = pageState.currentPage
                                    )
                            }

                        }
                    }
                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
private fun ImageWithDeleteIcon(
    userId: String,
    internetEnabled: Boolean,
    imagePath: String,
    imagePathList: List<String>,
    onDeleteClick: () -> Unit,
    downloadImage: (imagePath: String, imageUserId: String, result: (Boolean) -> Unit) -> Unit,

    slideState: SlideState,
    updateSlideState: (tripIdx: Int, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit
){

    var cardWidthInt: Int
    val cardWidthDp = 105.dp
    var spacerWidthInt: Int
    val spacerWidthDp = 16.dp

    with(LocalDensity.current){
        cardWidthInt = cardWidthDp.toPx().toInt()
        spacerWidthInt = spacerWidthDp.toPx().toInt()
    }


    var isDragged by remember { mutableStateOf(false) }

    val zIndex = if (isDragged) 1.0f else 0.0f

    val horizontalTranslation by animateIntAsState(
        targetValue = when (slideState){
            SlideState.UP   -> -(cardWidthInt + spacerWidthInt)
            SlideState.DOWN -> cardWidthInt + spacerWidthInt
            else -> 0
        },
        label = "horizontal translation"
    )

    //card x offset

    val scale by animateFloatAsState(
        targetValue = if (isDragged) 1.05f else 1f,
        label = "scale"
    )

    val dragModifier = Modifier
        .offset { IntOffset(horizontalTranslation, 0) }
        .scale(scale)
        .zIndex(zIndex)

    MyCard(
        shape = MaterialTheme.shapes.small,
        modifier = dragModifier
            .size(cardWidthDp)
            .dragAndDropHorizontal(
                item = imagePath,
                items = imagePathList,
                itemWidth = cardWidthInt + spacerWidthInt,
                updateSlideState = updateSlideState,
                onStartDrag = {
                    isDragged = true
                },
                onStopDrag = { currentIndex, destinationIndex ->
                    isDragged = false

                    if (currentIndex != destinationIndex) {
                        updateItemPosition(currentIndex, destinationIndex)
                    }
                },
                isDraggedAfterLongPress = true
            )

    ) {
        Box {
            ImageFromFile(
                internetEnabled = internetEnabled,
                imageUserId = userId,
                imagePath = imagePath,
                contentDescription = stringResource(id = R.string.image),
                downloadImage = downloadImage,
                modifier = Modifier.fillMaxSize()
            )

            Column {
                MySpacerColumn(height = 4.dp)

                //delete icon
                Row {
                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                //TODO animation
                                onDeleteClick()
                            }
                    ) {
                        DisplayIcon(icon = MyIcons.deleteImage)
                    }

                    MySpacerRow(width = 4.dp)
                }
            }
        }
    }
}

@Composable
private fun ImageIndicateDots(
    pageCount:Int,
    currentPage: Int
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(20.dp)
                .clip(CircleShape)
                .background(n70.copy(alpha = 0.4f)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) {

                //current image dot
                if (currentPage == it){
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .size(10.dp)
                    )
                }

                //other dots
                else{
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(n80.copy(alpha = 0.7f))
                                .size(8.dp)
                        )
                    }
                }
            }
        }

        MySpacerColumn(height = 8.dp)
    }
}
























@Composable
@PreviewLightDark
private fun ImageCardPreview(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            ImageCard(
                imageUserId = "",
                internetEnabled = true,
                isEditMode = false,
                imagePathList = listOf("", "", ""),
                onClickImage = {},
                onAddImages = {},
                deleteImage = {},
                isOverImage = {},
                reorderImageList = {_,_ ->},
                downloadImage = {_,_,_ ->},
                saveImageToInternalStorage = {_,_ -> ""}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun ImageCardPreviewEdit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            ImageCard(
                imageUserId = "",
                internetEnabled = true,
                isEditMode = true,
                imagePathList = listOf("", "", ""),
                onClickImage = {},
                onAddImages = {},
                deleteImage = {},
                isOverImage = {},
                reorderImageList = {_,_ ->},
                downloadImage = {_,_,_ ->},
                saveImageToInternalStorage = {_,_ -> ""}
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun ImageCardPreviewEdit1(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            ImageCard(
                imageUserId = "",
                internetEnabled = true,
                isEditMode = true,
                imagePathList = listOf(),
                onClickImage = {},
                onAddImages = {},
                deleteImage = {},
                isOverImage = {},
                reorderImageList = {_,_ ->},
                downloadImage = {_,_,_ ->},
                saveImageToInternalStorage = {_,_ -> ""}
            )
        }
    }
}
