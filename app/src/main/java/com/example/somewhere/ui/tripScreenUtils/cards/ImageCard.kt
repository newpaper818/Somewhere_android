package com.example.somewhere.ui.tripScreenUtils.cards

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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.theme.ColorType
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getColor
import com.example.somewhere.ui.theme.getTextStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.DecimalFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt

private const val IMAGE_MAX_COUNT = 10
private const val IMAGE_MAX_SIZE_MB = 2.5    //Mebibyte

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    tripId: Int,
    isEditMode: Boolean,
    imgList: List<String>,
    onAddImages: (List<String>) -> Unit,
    deleteImage: (String) -> Unit,
    isOverImage: (Boolean) -> Unit,

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

    val addImageTextStyle = if (!isImageCountLimit && imgList.size < IMAGE_MAX_COUNT) titleTextStyle.copy(color = MaterialTheme.colorScheme.primary)
                            else titleTextStyle

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ){ uriList ->
        var addUriList = uriList

        if (imgList.size + uriList.size > IMAGE_MAX_COUNT && !isImageCountLimit){
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
        visible = isEditMode || imgList.isNotEmpty(),
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
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
                                text = stringResource(id = R.string.image_card_title, imgList.size, IMAGE_MAX_COUNT),
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
                                        enabled = !isImageCountLimit && imgList.size < IMAGE_MAX_COUNT,
                                        onClick = {
                                            galleryLauncher.launch("image/*")
                                        }
                                    )
                                    .padding(16.dp, 14.dp, 16.dp, 8.dp)
                            )
                        }

                        //if no image
                        if (imgList.isEmpty()) {
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
                            item{
                                imgList.forEach {
                                    ImageWithDeleteIcon(
                                        imagePath = it,
                                        onDeleteClick = {
                                            deleteImage(it)
                                            Log.d("image", "img size = ${imgList.size}")
                                            if (imgList.size - 1 <= IMAGE_MAX_COUNT && isImageCountLimit){
                                                Log.d("image", "to false")
                                                isImageCountLimit = false
                                                isOverImage(false)
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
                            pageCount = imgList.size,
                            state = pageState,
                            beyondBoundsPageCount = 3
                        ) {
                            DisplayImage(imagePath = imgList[it])
                        }

                        if (imgList.size != 1)
                            ImageIndicateDots(
                                pageCount = imgList.size,
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
    onDeleteClick: () -> Unit
){
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.size(105.dp)
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

    val painter = rememberAsyncImagePainter(model = File(context.filesDir, imagePath))
    val imageState = painter.state

    val alphaTransition by animateFloatAsState(
        targetValue = if (imageState is AsyncImagePainter.State.Success) 1f else 0f,
        animationSpec = tween(300),
        label = ""
    )
    val scaleTransition by animateFloatAsState(
        targetValue = if (imageState is AsyncImagePainter.State.Success) 1f else 0.9f,
        animationSpec = tween(300),
        label = ""
    )

    Image(
        painter = painter,
        contentDescription = stringResource(id = R.string.image_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaTransition)
            .scale(scaleTransition)
    )

    //TODO prettier
    if (imageState is AsyncImagePainter.State.Loading){
        Text(
            text = "loading...",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
    if (imageState is AsyncImagePainter.State.Error){
        Text(
            text = "Error!",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
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






