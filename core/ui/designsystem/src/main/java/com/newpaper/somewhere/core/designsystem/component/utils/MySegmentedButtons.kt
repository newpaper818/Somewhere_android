package com.newpaper.somewhere.core.designsystem.component.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.SelectSwitchIcon
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.designsystem.R

data class SegmentedButtonItem(
    val icon: MyIcon? = null,
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun MySegmentedButtons(
    initSelectedItemIndex: Int,
    itemList: List<SegmentedButtonItem>,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
){
    val density = LocalDensity.current
    var width by rememberSaveable { mutableIntStateOf(100) }

    var selectedItemIndex by rememberSaveable { mutableIntStateOf(initSelectedItemIndex) }

    val selectedItemOffset = animateFloatAsState(
        targetValue = (width / itemList.size * selectedItemIndex).toFloat(),
        animationSpec = tween(150),
        label = "segmented button"
    )

    MyCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Box(
            modifier = Modifier.height(IntrinsicSize.Min)
        ){
            //selected item
            Box(
                modifier = modifier
                    .offset(selectedItemOffset.value.dp)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(10.dp))

                    .width(width.dp / itemList.size)
                    .fillMaxHeight()

                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )

            //item buttons
            Row(
                modifier = Modifier
                    .padding(6.dp)
                    .onSizeChanged {
                        width = with(density) { it.width.toDp().value.toInt() }
                    }
            ) {
                itemList.forEachIndexed { index, it ->
                    MySegmentedButtonItem(
                        isSelected = index == selectedItemIndex,
                        segmentedButtonItem = SegmentedButtonItem(
                            icon = it.icon,
                            text = it.text,
                            onClick = {
                                selectedItemIndex = index
                                it.onClick()
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MySegmentedButtonItem(
    isSelected: Boolean,
    segmentedButtonItem: SegmentedButtonItem,
    modifier: Modifier = Modifier,
){
    val noRippleConfiguration = RippleConfiguration(
        color = MaterialTheme.colorScheme.surfaceContainerLow
    )

    val containerColor = Color.Transparent

    val contentColor = animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(150),
        label = "content color"
    )

    val contentScale = animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.92f,
        animationSpec = tween(150),
        label = "content scale"
    )

    val haptic = LocalHapticFeedback.current

    val selected = stringResource(id = R.string.selected)
    val unselected = stringResource(id = R.string.not_selected)

    CompositionLocalProvider(LocalRippleConfiguration provides noRippleConfiguration) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(10.dp))
                .semantics {
                    stateDescription = if (isSelected) selected else unselected
                }
                .clickable(
                    onClickLabel = stringResource(id = R.string.select)
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    segmentedButtonItem.onClick()
                }
        ) {
            MyCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                    contentColor = contentColor.value
                )
            ) {
                Column(
                    modifier = Modifier
                        .scale(contentScale.value)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (segmentedButtonItem.icon != null) {
                        DisplayIcon(icon = segmentedButtonItem.icon)

                        MySpacerColumn(height = 4.dp)
                    }

                    Text(
                        text = segmentedButtonItem.text,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}





















@Composable
@PreviewLightDark
private fun NavigationDrawerPreview(){
    SomewhereTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
        ) {
            MySegmentedButtons(
                modifier = Modifier.padding(16.dp),
                initSelectedItemIndex = 0,
                itemList = listOf(
                    SegmentedButtonItem(
                        icon = SelectSwitchIcon.allowEdit,
                        text = "Preview",
                        onClick = { }
                    ),
                    SegmentedButtonItem(
                        icon = SelectSwitchIcon.viewOnly,
                        text = "is weired",
                        onClick = { }
                    )
                )
            )
        }
    }
}

