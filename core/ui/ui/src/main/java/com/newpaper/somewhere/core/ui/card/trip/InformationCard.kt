package com.newpaper.somewhere.core.ui.card.trip

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.component.utils.ClickableBox
import com.newpaper.somewhere.core.designsystem.component.utils.MyCard
import com.newpaper.somewhere.core.designsystem.component.utils.MySpacerRow
import com.newpaper.somewhere.core.designsystem.icon.DisplayIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcon
import com.newpaper.somewhere.core.designsystem.icon.MyIcons
import com.newpaper.somewhere.core.designsystem.theme.SomewhereTheme
import com.newpaper.somewhere.core.ui.ui.R

data class InformationCardItem(
    val icon: MyIcon,
    val text: String? = null,
    @StringRes val subTextId: Int? = null,
    val onClick: (() -> Unit)? = null,
){
    //deep copy
    fun copy(
        text: String? = null,
        onClick: (() -> Unit)? = null
    ): InformationCardItem {
        return InformationCardItem(icon, text, subTextId, onClick)
    }
}

val dateItem = InformationCardItem(icon = MyIcons.date, subTextId = R.string.dialog_title_select_date)
val spotTypeItem = InformationCardItem(icon = MyIcons.category, subTextId = R.string.dialog_title_set_spot_type)
val currencyItem = InformationCardItem(icon = MyIcons.budget, subTextId = R.string.dialog_title_set_currency_type)
val budgetItem = InformationCardItem(icon = MyIcons.budget, subTextId = R.string.dialog_title_set_budget)
val travelDistanceItem = InformationCardItem(icon = MyIcons.travelDistance, subTextId = R.string.dialog_title_set_travel_distance)

@Composable
fun InformationCard(
    isEditMode: Boolean,
    list: List<InformationCardItem>,

    modifier: Modifier = Modifier,
){
    //information card
    MyCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            list.forEach{
                if (it.text != null) {
                    IconTextRow(
                        isVisible = !isEditMode || it.onClick != null,
                        isClickable = isEditMode && it.onClick != null,
                        informationItem = it
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IconTextRow(
    isVisible: Boolean,
    isClickable: Boolean,
    informationItem: InformationCardItem
) {

    var itemHeight by rememberSaveable { mutableIntStateOf(0) }
    val density = LocalDensity.current

    LaunchedEffect(informationItem.text){
        itemHeight = 0
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
    ) {

        ClickableBox(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .heightIn(min = 48.dp, max = 100.dp)
                .fillMaxWidth(),
            enabled = isClickable,
            onClick = informationItem.onClick ?: { }
        ) {
            FlowRow(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .onSizeChanged { with(density) { itemHeight = it.height.toDp().value.toInt() } },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //icon
                    Box(
                        modifier = Modifier.size(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DisplayIcon(icon = informationItem.icon)
                    }

                    MySpacerRow(width = 16.dp)

                    //text
                    Text(
                        text = informationItem.text ?: "",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (isClickable) {
                    Row(
                        modifier = Modifier.height(30.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MySpacerRow(width = 6.dp)

                        if (itemHeight > 50)
                            Spacer(modifier = Modifier.weight(1f))

                        //sub text
                        if (informationItem.subTextId != null)
                            Text(
                                text = stringResource(id = informationItem.subTextId),
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                maxLines = 1
                            )

                        MySpacerRow(width = 4.dp)

                        //clickable icon
                        DisplayIcon(
                            icon = MyIcons.clickableItem,
                            contentDescriptionIsNull = true
                        )
                    }
                }
            }

        }
    }
}






















@Composable
@PreviewLightDark
private fun Preview_Trip(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            InformationCard(
                isEditMode = false,
                list = listOf(
                    currencyItem.copy(text = "$ 1,000", onClick = {}),
                    travelDistanceItem.copy(text = "120 km", onClick = {})
                )
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_Trip_Edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            InformationCard(
                isEditMode = true,
                list = listOf(
                    currencyItem.copy(text = "$ 1,000", onClick = {}),
                    travelDistanceItem.copy(text = "120 km")
                )
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_Date(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            InformationCard(
                isEditMode = false,
                list = listOf(
                    budgetItem.copy(text = "$ 1,000"),
                    travelDistanceItem.copy(text = "1,234 km")
                )
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_Spot(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            InformationCard(
                isEditMode = false,
                list = listOf(
                    spotTypeItem.copy(text = "Tour", onClick = {}),
                    budgetItem.copy(text = "$ 1,000", onClick = {}),
                    travelDistanceItem.copy(text = "111 km", onClick = {})
                )
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun Preview_Spot_Edit(){
    SomewhereTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            InformationCard(
                isEditMode = true,
                list = listOf(
                    spotTypeItem.copy(text = "Tour", onClick = {}),
                    budgetItem.copy(text = "$ 1,000", onClick = {}),
                    travelDistanceItem.copy(text = "111 km", onClick = {})
                )
            )
        }
    }
}