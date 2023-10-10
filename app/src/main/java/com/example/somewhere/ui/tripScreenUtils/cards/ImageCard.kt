package com.example.somewhere.ui.tripScreenUtils.cards

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.somewhere.R
import com.example.somewhere.ui.commonScreenUtils.MySpacerColumn
import com.example.somewhere.ui.commonScreenUtils.MySpacerRow
import com.example.somewhere.ui.commonScreenUtils.DisplayIcon
import com.example.somewhere.ui.commonScreenUtils.MyIcons
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt

const val IMAGE_MAX_COUNT = 10
const val IMAGE_MAX_SIZE = 45_500_000

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    tripId: Int,
    isEditMode: Boolean,
    imgList: List<String>,
    onAddImages: (List<String>) -> Unit,
    deleteImage: (String) -> Unit,
    showSnackBar: (text: String, actionLabel: String?, duration: SnackbarDuration) -> Unit,

    modifier: Modifier = Modifier,

    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    val context = LocalContext.current

    val snackBarTextImageLimit = stringResource(id = R.string.snack_bar_image_limit)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ){ uriList ->
        var addUriList = uriList

        if (imgList.size + uriList.size > IMAGE_MAX_COUNT){
            addUriList = addUriList.subList(0, IMAGE_MAX_COUNT - imgList.size)

            showSnackBar(snackBarTextImageLimit, null, SnackbarDuration.Short)
        }

        val fileList: MutableList<String> = mutableListOf()

        addUriList.forEachIndexed { index, uri ->
            val file = saveImageToInternalStorage(tripId, index, context, uri)
            fileList.add(file)
        }


        onAddImages(fileList)
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
                modifier = modifier1.fillMaxWidth()
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
                                text = stringResource(id = R.string.image_card_title, imgList.size),
                                style = titleTextStyle,
                                modifier = Modifier.padding(16.dp, 14.dp, 0.dp, 8.dp),
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            //add images text

                            Text(
                                text = stringResource(id = R.string.image_card_subtitle_add_images),
                                style = titleTextStyle.copy(color = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .clickable {
                                        if (imgList.size <= 9)
                                            galleryLauncher.launch("image/*")
                                        else
                                            showSnackBar(
                                                snackBarTextImageLimit,
                                                null,
                                                SnackbarDuration.Short
                                            )
                                    }
                                    .padding(16.dp, 14.dp, 16.dp, 8.dp)
                            )
                        }

                        //if no images
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
                            items(imgList) {
                                Card(
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.size(105.dp)
                                ) {
                                    Box {
                                        DisplayImage(imagePath = it)


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
                                                        .clickable { deleteImage(it) }
                                                ) {
                                                    DisplayIcon(icon = MyIcons.deleteImage)
                                                }

                                                MySpacerRow(width = 4.dp)
                                            }
                                        }
                                    }
                                }
                                MySpacerRow(width = 16.dp)
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
                            state = pageState
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
fun DisplayImage(
    imagePath: String
){
    val context = LocalContext.current

    val bitmap = loadImageFromInternalStorage(context, imagePath)
//    if (bitmap != null){

    if (bitmap != null){
//        AsyncImage(
//            model = ImageRequest.Builder(context).data(bitmap).build(),
//            contentDescription = "image",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier.fillMaxSize()
//        )
        AsyncImage(
            model = ImageRequest.Builder(context).data(bitmap).crossfade(true).build(),
            contentDescription = "",
//            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
//            error = painterResource(id = R.drawable.ic_launcher_foreground),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    else {
        Text(text = "null")
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
): String {
    //convert to bitmap
    val bitmap = getBitMapFromUri(uri, context)

    //resize bitmap (under 3MB)
    compressBitmap(bitmap)

    //make file name : tripId date time index
    val fileName = getImageFileName(tripId, index)
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return fileName
}

private fun getImageFileName(
    tripId: Int,
    index: Int,
): String {
    val now = ZonedDateTime.now(ZoneId.of("UTC"))
    val dateTime = now.format(DateTimeFormatter.ofPattern("yyMMdd_HHmmssS"))
    return "somewhere_${tripId}_utc${dateTime}_${index}.jpg"
}

fun loadImageFromInternalStorage(
    context: Context,
    filePath: String
): Bitmap? {
    Log.d("img", filePath)
    try {
        val inputStream = context.openFileInput(filePath)
        return BitmapFactory.decodeStream(inputStream)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return null
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
    bitmap: Bitmap,
    uri: Uri,
    context: Context
): Bitmap{

    val size = bitmap.byteCount
    val width = bitmap.width
    val height = bitmap.height

    Log.d("img", "before = ${size}byte ${width}x${height}")

    //if is over 3MB (== 45,500,000 ?)
    return if (size > IMAGE_MAX_SIZE){
        var scale = sqrt((IMAGE_MAX_SIZE / size).toFloat())
        45,500,000 / 45,543,680
        Log.d("img", "scale = $scale | $IMAGE_MAX_SIZE / $size")
        if (scale > 0.1){
            scale = 0.1f
        }

        val newBitmap = Bitmap.createScaledBitmap(bitmap, (width * scale).toInt(), (height *scale).toInt(), true)
        Log.d("img", "after = ${newBitmap.byteCount}byte ${newBitmap.width}x${newBitmap.height}")

        newBitmap
    }
    else
        bitmap
}


private fun createImageFile(
    fileName: String = "temp_image"
): File {
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(fileName, ".jpg", storageDir)
}


private fun uriToFile(
    context: Context,
    uri: Uri,
    fileName: String
): File?{
    context.contentResolver.openInputStream(uri)?.let {inputStream ->

        val tempFile: File = createImageFile(fileName)
        val fileOutputStream = FileOutputStream(tempFile)

        inputStream.copyTo(fileOutputStream)
        inputStream.close()
        fileOutputStream.close()

        return tempFile
    }
    return null
}

private fun compressImage(
    filePath: String,
    targetMB: Double = 1.0
){
    var image: Bitmap = BitmapFactory.decodeFile(filePath)

    val exif = ExifInterface(filePath)
    val exifOrientation: Int = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
    )

    val exifDegree: Int = exifOrientationToDegrees(exifOrientation)

    image = rotateImage(image, exifDegree.toFloat())

    try {
        val file = File(filePath)
        val length = file.length()

        val fileSizeInKB = (length / 1024).toString().toDouble()
        val fileSizeInMB = (fileSizeInKB / 1024).toString().toDouble()

        var quality = 100
        if (fileSizeInMB > targetMB){
            quality = ((targetMB / fileSizeInMB) * 100).toInt()
        }

        val fileOutputStream = FileOutputStream(filePath)
        //compress
        image.compress(Bitmap.)
        fileOutputStream.close()
    }
    catch (){

    }
}

private fun exifOrientationToDegrees(exifOrientation: Int): Int{
    return when (exifOrientation){
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}

private fun rotateImage(
    source: Bitmap,
    angle: Float
): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}





