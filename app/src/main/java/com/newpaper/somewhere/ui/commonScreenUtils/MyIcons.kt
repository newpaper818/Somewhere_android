package com.newpaper.somewhere.ui.commonScreenUtils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.R
import com.newpaper.somewhere.ui.theme.gray
import com.newpaper.somewhere.ui.theme.white

data class MyIcon(
    val imageVector: ImageVector,
    val size: Dp,
    val color: Color?,  /**if null, set [color] to Material.colors.onSurface, onBackground...*/
    @StringRes val descriptionTextId: Int?
)

//MyIcons collection
object MyIcons {

    //
    val delete = MyIcon(Icons.Rounded.Delete,            22.dp, gray, R.string.delete)
    val deleteButton = MyIcon(Icons.Rounded.Delete,      24.dp, gray, R.string.delete)

    //    val add = MyIcon(Icons.Filled.Add,                  22.dp, null, R.string.new_)
    val deleteImage = MyIcon(Icons.Rounded.Close,        16.dp, null, R.string.delete_image)
    val dragHandle = MyIcon(Icons.Rounded.DragHandle,    22.dp, gray, null)

    val clickableItem = MyIcon(Icons.Rounded.NavigateNext,    22.dp, gray, R.string.drag_handle)

    //
    val menu = MyIcon(Icons.Rounded.Menu,      22.dp, null, R.string.menu)
    val back = MyIcon(Icons.Rounded.ArrowBack, 22.dp, null, R.string.back)
    val close = MyIcon(Icons.Rounded.Close,    22.dp, null, R.string.close)
    val edit = MyIcon(Icons.Rounded.Edit,      24.dp, null, R.string.edit)

//    val more = MyIcon(Icons.Filled.MoreVert,  22.dp, null, R.string.more)
//    val done = MyIcon(Icons.Filled.Done,  22.dp, null, R.string.save)

    //bottom navigation bar
    val myTripsFilled = MyIcon(Icons.Rounded.Luggage,            24.dp, null, R.string.my_trips)
    val myTripsOutlined = MyIcon(Icons.Outlined.Luggage,        24.dp, null, R.string.my_trips)

    val moreHorizFilled = MyIcon(Icons.Rounded.MoreHoriz,        24.dp, null, R.string.more)
    val moreHorizOutlined = MyIcon(Icons.Outlined.MoreHoriz,    24.dp, null, R.string.more)

//    val profileNotSelected = MyIcon(Icons.Outlined.Person,  24.dp, null, R.string.profile)
//    val profileSelected = MyIcon(Icons.Filled.Person,       24.dp, null, R.string.profile)


    //Floating Action Button
    val add = MyIcon(Icons.Rounded.Add,        24.dp, null, R.string.new_)

    //image loading error
    val imageLoadingError = MyIcon(Icons.Rounded.Error,        36.dp, null, R.string.image_loading_error)

    val setColor = MyIcon(Icons.Outlined.Palette,  24.dp, white, R.string.set_color)
    //color dialog
    val selectedColor = MyIcon(Icons.Rounded.Done,  22.dp, white, R.string.selected_color)

    //date < >
    val dateLeftArrow = MyIcon(Icons.Rounded.KeyboardArrowLeft,   30.dp, null, R.string.previous_date)
    val dateRightArrow = MyIcon(Icons.Rounded.KeyboardArrowRight, 30.dp, null, R.string.next_date)

    val spotLeftArrow = MyIcon(Icons.Rounded.KeyboardArrowLeft,   30.dp, null, R.string.previous_spot)
    val spotRightArrow = MyIcon(Icons.Rounded.KeyboardArrowRight, 30.dp, null, R.string.next_spot)

    //date time
    val date = MyIcon(Icons.Rounded.CalendarMonth,   22.dp, gray, R.string.date)
    val time = MyIcon(Icons.Rounded.AccessTime,      20.dp, gray, R.string.time)
    val rightArrowTo = MyIcon(Icons.Rounded.East, 22.dp, gray, R.string.to)

    //set time
    val switchToTextInput = MyIcon(Icons.Outlined.Keyboard,   22.dp, gray, R.string.switch_to_text_input)
    val switchToTouchInput = MyIcon(Icons.Rounded.Schedule,   22.dp, gray, R.string.switch_to_touch_input)


    //item expand collapse
    val expand = MyIcon(Icons.Rounded.ExpandMore,   22.dp, gray, R.string.expand)
    val collapse = MyIcon(Icons.Rounded.ExpandLess, 22.dp, gray, R.string.collapse)

    //information card
    val period = MyIcon(Icons.Rounded.SyncAlt,       20.dp, gray, R.string.period)
    val budget = MyIcon(Icons.Rounded.Payments,      22.dp, gray, R.string.budget)
    val travelDistance = MyIcon(Icons.Rounded.Route, 20.dp, gray, R.string.travel_distance)
    val category = MyIcon(Icons.Rounded.Bookmarks,   20.dp, gray, R.string.category)

    //map
    val map = MyIcon(Icons.Rounded.Map,                             22.dp, null, R.string.map)
    val focusOnToTarget = MyIcon(Icons.Rounded.LocationOn,          24.dp, null, R.string.focus_on_to_target)
    val disabledFocusOnToTarget = MyIcon(Icons.Rounded.LocationOn,  24.dp, gray, R.string.disabled_focus_on_to_target)
    val fullscreen = MyIcon(Icons.Rounded.Fullscreen,               24.dp, null, R.string.fullscreen_map)
    val myLocation = MyIcon(Icons.Rounded.MyLocation,               24.dp, null, R.string.my_location)
    val disabledMyLocation = MyIcon(Icons.Rounded.LocationDisabled, 24.dp, gray, R.string.disabled_my_location)
//    val expandControlBox = MyIcon(Icons.Filled.ExpandLess,         24.dp, null, R.string.expand_control_box)
//    val collapseControlBox = MyIcon(Icons.Filled.ExpandMore,       24.dp, null, R.string.collapse_control_box)

    val zoomOut = MyIcon(Icons.Rounded.Remove,                   26.dp, null, R.string.zoom_out)
    val zoomOutLess = MyIcon(Icons.Rounded.Remove,                       18.dp, null, R.string.zoom_out_less)
    val zoomInLess = MyIcon(Icons.Rounded.Add,                           18.dp, null, R.string.zoom_in_less)
    val zoomIn = MyIcon(Icons.Rounded.Add,                       26.dp, null, R.string.zoom_in)

    val editLocation = MyIcon(Icons.Rounded.Edit,        22.dp, null, R.string.edit_location)
    val deleteLocation = MyIcon(Icons.Rounded.Delete,    22.dp, null, R.string.delete_location)
    val cancelEditLocation = MyIcon(Icons.Rounded.Close, 22.dp, null, R.string.cancel_edit_location)
    val saveLocation = MyIcon(Icons.Rounded.Done,        22.dp, null, R.string.save_location)

    //setting
    val openInNew = MyIcon(Icons.Rounded.OpenInNew,        22.dp, gray, R.string.open_in_new)

}

//draw icon
@Composable
fun DisplayIcon(
    icon: MyIcon,

    size: Dp? = null,

    enabled: Boolean = true,    /**color priority: [enabled] > [onColor] > [color]**/
    onColor: Boolean = false,   /**if true, set [color] to Material.colors.onSurface, onBackground...*/
    color: Color? = null,

    @StringRes descriptionTextId: Int? = null
){
    val imageVector = icon.imageVector
    val contentDescription =
        if (icon.descriptionTextId == null) null
        else stringResource(id = descriptionTextId ?: icon.descriptionTextId)

    val modifier = Modifier.size(size ?: icon.size)

    if (!enabled) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = gray
        )
    }

    else if (onColor || icon.color == null) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier
        )
    }
    else {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = color ?: icon.color
        )
    }
}