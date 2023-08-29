package com.example.somewhere.ui.screenUtils.cards

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.somewhere.R
import com.example.somewhere.ui.screenUtils.DisplayIcon
import com.example.somewhere.ui.screenUtils.MyIcons
import com.example.somewhere.ui.screenUtils.MySpacerColumn
import com.example.somewhere.ui.screenUtils.MySpacerRow
import com.example.somewhere.ui.theme.TextType
import com.example.somewhere.ui.theme.getTextStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    isEditMode: Boolean,
    imgList: List<String>,
    onAddImages: (List<String>) -> Unit,
    deleteImage: (String) -> Unit,
    modifier: Modifier = Modifier,

    titleTextStyle: TextStyle = getTextStyle(TextType.CARD__TITLE),
    bodyNullTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
){
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ){uriList ->
        onAddImages(uriList.map { it.toString() })
    }

    AnimatedVisibility(
        visible = isEditMode || imgList.isNotEmpty(),
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
    ) {

        Column(
            modifier = modifier
        ) {
            Card(
                elevation = 0.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .fillMaxWidth()
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
                            val toastText = stringResource(id = R.string.toast_image_limit)

                            Text(
                                text = stringResource(id = R.string.image_card_subtitle_add_images),
                                style = titleTextStyle,
                                modifier = Modifier
                                    .clickable {
                                        if (imgList.size <= 9)
                                            galleryLauncher.launch("image/*")
                                        else
                                            Toast
                                                .makeText(
                                                    context,
                                                    toastText,
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
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
                                    modifier = Modifier
                                        .size(105.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    elevation = 0.dp
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
                                                        .background(MaterialTheme.colors.secondary)
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
    val imagePainter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data = Uri.parse(imagePath))
            .build()
    )

    Image(
        painter = imagePainter,
        contentDescription = stringResource(id = R.string.image_card_display_image_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
    )

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