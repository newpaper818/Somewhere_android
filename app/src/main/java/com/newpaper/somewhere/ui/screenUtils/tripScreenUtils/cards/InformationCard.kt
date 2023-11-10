package com.newpaper.somewhere.ui.screenUtils.tripScreenUtils.cards

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.ClickableBox
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MySpacerRow
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.DisplayIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcon
import com.newpaper.somewhere.ui.screenUtils.commonScreenUtils.MyIcons
import com.newpaper.somewhere.ui.theme.TextType
import com.newpaper.somewhere.ui.theme.getTextStyle

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

val dateItem = InformationCardItem(icon = MyIcons.date, subTextId = R.string.move_date)
val spotTypeItem = InformationCardItem(icon = MyIcons.category, subTextId = R.string.select_spot_type)
val currencyItem = InformationCardItem(icon = MyIcons.budget, subTextId = R.string.set_currency_type)
val budgetItem = InformationCardItem(icon = MyIcons.budget, subTextId = R.string.set_budget)
val travelDistanceItem = InformationCardItem(icon = MyIcons.travelDistance, subTextId = R.string.set_travel_distance)

@Composable
fun InformationCard(
    isEditMode: Boolean,
    list: List<InformationCardItem>,

    modifier: Modifier = Modifier,
){
    //information card
    Card(
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

@Composable
fun IconTextRow(
    isVisible: Boolean,
    isClickable: Boolean,
    informationItem: InformationCardItem,

    textStyle: TextStyle = getTextStyle(TextType.CARD__BODY),
    subTextStyle: TextStyle = getTextStyle(TextType.CARD__BODY_NULL)
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(tween(300)),
        exit = shrinkVertically(tween(300))
    ) {

        ClickableBox(
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.height(48.dp),
            enabled = informationItem.onClick != null,
            onClick = informationItem.onClick ?: { }

        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
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
                    style = textStyle
                )

                if (isClickable) {
                    Spacer(modifier = Modifier.weight(1f))

                    //sub text
                    if (informationItem.subTextId != null)
                        Text(
                            text = stringResource(id = informationItem.subTextId),
                            style = subTextStyle
                        )

                    MySpacerRow(width = 4.dp)

                    //clickable icon
                    DisplayIcon(icon = MyIcons.clickableItem)
                }
            }
        }
    }
}