package com.example.somewhere.ui.screenUtils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.somewhere.R

data class MyIcon(
    val imageVector: ImageVector,
    val size: Dp,
    val color: Color?,  /**if null, set [color] to Material.colors.onSurface, onBackground...*/
    @StringRes val descriptionTextId: Int
)

object IconColor{
    val gray = Color(0xff717171)    //middle of onSurface(dark, light)
}

//MyIcons collection
object MyIcons {

    //
    val delete = MyIcon(Icons.Filled.Delete,  22.dp, IconColor.gray, R.string.delete)
    val add = MyIcon(Icons.Filled.Add,        22.dp, null, R.string.new_)
    val deleteImage = MyIcon(Icons.Filled.Close,        16.dp, null, R.string.delete_image)

    //app bar
    val menu = MyIcon(Icons.Filled.Menu,      22.dp, null, R.string.menu)
    val back = MyIcon(Icons.Filled.ArrowBack, 22.dp, null, R.string.back)
    val edit = MyIcon(Icons.Filled.Edit,      22.dp, null, R.string.edit)
    val more = MyIcon(Icons.Filled.MoreVert,  22.dp, null, R.string.more)
    val done = MyIcon(Icons.Filled.Done,  22.dp, null, R.string.save)

    //date < >
    val leftArrow = MyIcon(Icons.Filled.KeyboardArrowLeft,   30.dp, null, R.string.previous_date)
    val rightArrow = MyIcon(Icons.Filled.KeyboardArrowRight, 30.dp, null, R.string.next_date)
    val toFirstPage = MyIcon(Icons.Filled.FirstPage,         30.dp, null, R.string.to_first_date)
    val toLastPage = MyIcon(Icons.Filled.LastPage,           30.dp, null, R.string.to_last_date)
    val disabledLeftArrow = MyIcon(Icons.Filled.KeyboardArrowLeft,   30.dp, IconColor.gray, R.string.disabled_previous_date)
    val disabledRightArrow = MyIcon(Icons.Filled.KeyboardArrowRight, 30.dp, IconColor.gray, R.string.disabled_next_date)
    val disabledToFirstPage = MyIcon(Icons.Filled.FirstPage,         30.dp, IconColor.gray, R.string.disabled_to_first_date)
    val disabledToLastPage = MyIcon(Icons.Filled.LastPage,           30.dp, IconColor.gray, R.string.disabled_to_last_date)

    //item expand collapse
    val expand = MyIcon(Icons.Filled.ExpandMore,   22.dp, IconColor.gray, R.string.expand)
    val collapse = MyIcon(Icons.Filled.ExpandLess, 22.dp, IconColor.gray, R.string.collapse)

    //information card
    val date = MyIcon(Icons.Filled.CalendarMonth,   22.dp, IconColor.gray, R.string.date)
    val period = MyIcon(Icons.Filled.SyncAlt,       20.dp, IconColor.gray, R.string.period)
    val budget = MyIcon(Icons.Filled.AttachMoney,   22.dp, IconColor.gray, R.string.budget)
    val travelDistance = MyIcon(Icons.Filled.Route, 20.dp, IconColor.gray, R.string.travel_distance)
    val time = MyIcon(Icons.Filled.AccessTime,      20.dp, IconColor.gray, R.string.time)
    val category = MyIcon(Icons.Filled.Bookmarks,   20.dp, IconColor.gray, R.string.category)

    //map
    val map = MyIcon(Icons.Filled.Map,                             22.dp, null, R.string.map)
    val focusOnToTarget = MyIcon(Icons.Filled.LocationOn,          24.dp, null, R.string.focus_on_to_target)
    val disabledFocusOnToTarget = MyIcon(Icons.Filled.LocationOn,  24.dp, IconColor.gray, R.string.disabled_focus_on_to_target)
    val fullscreen = MyIcon(Icons.Filled.Fullscreen,               24.dp, null, R.string.fullscreen_map)
    val myLocation = MyIcon(Icons.Filled.MyLocation,               24.dp, null, R.string.my_location)
    val disabledMyLocation = MyIcon(Icons.Filled.LocationDisabled, 24.dp, IconColor.gray, R.string.disabled_my_location)
    val expandControlBox = MyIcon(Icons.Filled.ExpandLess,         24.dp, null, R.string.expand_control_box)
    val collapseControlBox = MyIcon(Icons.Filled.ExpandMore,       24.dp, null, R.string.collapse_control_box)
}

//draw icon
@Composable
fun DisplayIcon(
    icon: MyIcon,
    onColor: Boolean = false,   /**if true, set [color] to Material.colors.onSurface, onBackground...*/
    size: Dp? = null,
    color: Color? = null,
    @StringRes descriptionTextId: Int? = null
){
    if(!onColor && icon.color != null ){
        androidx.compose.material.Icon(
            imageVector = icon.imageVector,
            contentDescription = stringResource(id = descriptionTextId ?: icon.descriptionTextId),
            modifier = Modifier.size(size ?: icon.size),
            tint = color ?: icon.color
        )
    }
    else{
        androidx.compose.material.Icon(
            imageVector = icon.imageVector,
            contentDescription = stringResource(id = descriptionTextId ?: icon.descriptionTextId),
            modifier = Modifier.size(size ?: icon.size)
            //on color
        )
    }
}