package com.newpaper.somewhere.ui.tripScreenUtils.cards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.newpaper.somewhere.ui.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.theme.ColorType
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getColor
import com.newpaper.somewhere.ui.theme.getTextStyle
import com.newpaper.somewhere.utils.SlideState
import com.newpaper.somewhere.utils.dragAndDropHorizontal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Integer.min
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt

private const val IMAGE_MAX_COUNT = 10
private const val IMAGE_MAX_SIZE_MB = 2.5    //Mebibyte

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ImageCard(
    tripId: Int,
    isEditMode: Boolean,
    imagePathList: List<String>,
    onAddImages: (List<String>) -> Unit,
    deleteImage: (String) -> Unit,
    isOverImage: (Boolean) -> Unit,
    reorderImageList: (currentIndex: Int, destinationIndex: Int) -> Unit,

    modifier: Modifier = Modifier,

    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    errorTitleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE_ERROR),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    // 11 or over
    var isImageCountLimit by rememberSaveable { mutableStateOf(false) }

    val titleTextStyle1 = if (isImageCountLimit) errorTitleTextStyle
                        else titleTextStyle

    val borderColor = if (isImageCountLimit) getColor(ColorType.ERROR_BORDER)
                        else Color.Transparent

    val addImageTextStyle = if (!isImageCountLimit && imagePathList.size < IMAGE_MAX_COUNT) titleTextStyle.copy(color = MaterialTheme.colorScheme.primary)
                            else titleTextStyle

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ){ uriList ->
        var addUriList = uriList

        if (imagePathList.size + uriList.size > IMAGE_MAX_COUNT && !isImageCountLimit){
            isImageCountLimit = true
            isOverImage(true)
        }

        if (uriList.size > 20){
            addUriList = addUriList.subList(0, 20)
        }


        val fileList: MutableList<String> = mutableListOf()

        coroutineScope.launch {
            //save to internal storage
            addUriList.forEachIndexed { index, uri ->
                val file = saveImageToInternalStorage(tripId, index, context, uri)
                if (file != null)
                    fileList.add(file)
            }
            onAddImages(fileList)
        }
    }


    val modifier1 = if (isEditMode) modifier
                    else modifier.sizeIn(maxWidth = 390.dp, maxHeight = 390.dp)

    AnimatedVisibility(
        visible = isEditMode || imagePathList.isNotEmpty(),
        enter =
        scaleIn(animationSpec = tween(170, 0, LinearEasing))
                + expandVertically(animationSpec = tween(190, 0, LinearEasing))
                + fadeIn(animationSpec = tween(300, 100)),
        exit =
        scaleOut(animationSpec = tween(250, 100))
                + shrinkVertically(animationSpec = tween(320, 100))
                + fadeOut(animationSpec = tween(300, 100))
    ) {

        Column(
            modifier = modifier
        ) {
            Card(
                modifier = modifier1
                    .fillMaxWidth()
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            ) {
                //edit mode
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = fadeIn(animationSpec = tween(100, 0)),
                    exit = fadeOut(animationSpec = tween(500, 0))
                ) {

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            //Images text
                            Text(
                                text = stringResource(id = R.string.image_card_title, imagePathList.size, IMAGE_MAX_COUNT),
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

                        val slideStates = remember { mutableStateMapOf<String, SlideState>(
                            *imagePathList.map { it to SlideState.NONE }.toTypedArray()
                        ) }

                        //if no image
                        if (imagePathList.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.image_card_body_no_image),
                                style = bodyNullTextStyle,
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
                            items(imagePathList){imagePath ->

                                key(imagePathList){
                                    val slideState = slideStates[imagePath] ?: SlideState.NONE

                                    ImageWithDeleteIcon(
                                        imagePath = imagePath,
                                        imagePathList = imagePathList,
                                        onDeleteClick = {
                                            deleteImage(imagePath)
                                                //Log.d("image", "img size = ${imagePathList.size}")
                                            if (imagePathList.size - 1 <= IMAGE_MAX_COUNT && isImageCountLimit){
                                                //Log.d("image", "to false")
                                                isImageCountLimit = false
                                                isOverImage(false)
                                            }
                                        },
                                        slideState = slideState,
                                        updateSlideState = { imageIndex, newSlideState ->
                                            slideStates[imagePathList[imageIndex]] = newSlideState
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

                //not edit mode showing images
                AnimatedVisibility(
                    visible = !isEditMode,
                    enter = expandVertically(tween(400), expandFrom = Alignment.Bottom),
                    exit = shrinkVertically(
                        tween(350, easing = LinearEasing),
                        shrinkTowards = Alignment.Bottom
                    )
                ) {
                    val pageState = rememberPagerState()

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    ) {
                        HorizontalPager(
                            pageCount = imagePathList.size,
                            state = pageState,
                            beyondBoundsPageCount = 3
                        ) {
                            DisplayImage(imagePath = imagePathList[it])
                        }

                        if (imagePathList.size != 1)
                            ImageIndicateDots(
                                pageCount = imagePathList.size,
                                currentPage = pageState.currentPage
                            )
                    }

                }
            }

            MySpacerColumn(height = 16.dp)
        }
    }
}

@Composable
private fun ImageWithDeleteIcon(
    imagePath: String,
    imagePathList: List<String>,
    onDeleteClick: () -> Unit,

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

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = dragModifier.size(cardWidthDp)
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

                    if (currentIndex != destinationIndex){
                        updateItemPosition(currentIndex, destinationIndex)
                    }
                },
                isDraggedAfterLongPress = true
            )

    ) {
        Box {
            DisplayImage(imagePath = imagePath)

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
fun DisplayImage(
    imagePath: String
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
            .data(File(context.filesDir, imagePath))
            .crossfade(true)
            .crossfade(300)
            .build(),
        contentDescription = "image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
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
private fun OnLoadingImage(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .shimmerEffect()
    )
}

@Composable
private fun OnErrorImage(){
    var boxSize by remember{
        mutableStateOf(IntSize.Zero)
    }

    val minBoxLengthDp = with(LocalDensity.current){
        min(boxSize.height, boxSize.width).toDp()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .onSizeChanged { boxSize = it },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayIcon(icon = MyIcons.imageLoadingError)

            if (minBoxLengthDp > 160.dp) {
                MySpacerColumn(height = 8.dp)

                Text(
                    text = stringResource(id = R.string.image_loading_error),
                )
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember{
        mutableStateOf(IntSize.Zero)
    }

    val transition = rememberInfiniteTransition(label = "infinite_transition")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ),
        label = "shimmer_effect"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surfaceTint,
                MaterialTheme.colorScheme.surface
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

@Composable
private fun ImageIndicateDots(
    pageCount:Int,
    currentPage: Int
){
    Column {
        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) {
                val color =
                    if (currentPage == it) Color.LightGray
                    else Color.DarkGray

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
    }
}

private fun saveImageToInternalStorage(
    tripId: Int,
    index: Int,
    context: Context,
    uri: Uri
): String? {
    //convert uri to bitmap
    val bitmap = getBitMapFromUri(uri, context)

    //compress bitmap
    val newBitmap = compressBitmap(context, bitmap, uri)

    //make file name : tripId date time index
    val fileName = getImageFileName(tripId, index)

    //save
    return try{

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {stream ->
            if (!newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)){
                throw IOException("Couldn't save bitmap")
            }
        }
        fileName

    } catch(e: IOException){
        e.printStackTrace()
        null
    }
}

private fun getImageFileName(
    tripId: Int,
    index: Int,
): String {
    val df = DecimalFormat("000")
    val now = ZonedDateTime.now(ZoneId.of("UTC"))
    val dateTime = now.format(DateTimeFormatter.ofPattern("yyMMdd_HHmmssS"))
    return "${df.format(tripId)}_${dateTime}_${index}.jpg"
    //001_231011_1030121_0.jpg
}

suspend fun loadImageFromInternalStorage(
    context: Context,
    filePath: String
): Bitmap? {

    return withContext(Dispatchers.IO){
        try {
            val inputStream = context.openFileInput(filePath)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}

private fun deleteFileFromInternalStorage(
    context: Context,
    filePath: String
): Boolean{
    return try {
        context.deleteFile(filePath)
    } catch (e: Exception){
        e.printStackTrace()
        false
    }
}

fun deleteFilesFromInternalStorage(
    context: Context,
    imageFileList: List<String>
){
    imageFileList.forEach {
        deleteFileFromInternalStorage(context, it)
    }
}

private fun getBitMapFromUri(
    uri: Uri,
    context: Context
): Bitmap {
    return if (Build.VERSION.SDK_INT >= 28) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    else{
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

private fun compressBitmap(
    context: Context,
    bitmap: Bitmap,
    uri: Uri
): Bitmap{
    var imageFileSize: Float = 0f
    runBlocking {
        imageFileSize = getFileSizeFromUri(context, uri)
    }

    val width = bitmap.width
    val height = bitmap.height


    //if is over 3 Mebibyte
    return if (imageFileSize > IMAGE_MAX_SIZE_MB){
        var scale = sqrt(IMAGE_MAX_SIZE_MB / imageFileSize)

        if (scale < 0.01f){
            scale = 0.01
        }

        val newBitmap = Bitmap.createScaledBitmap(bitmap, (width * scale).toInt(), (height *scale).toInt(), true)

        newBitmap
    }
    else
        bitmap
}



private fun getFileSizeFromUri(
    context: Context,
    uri: Uri
): Float {

    var fileSize: Long = 0

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

        cursor.moveToFirst()

        fileSize = cursor.getLong(sizeIndex)
    }

    //Byte to Mebibyte
    return fileSize.toFloat() / 1024 / 1024
}






