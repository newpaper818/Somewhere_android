package com.example.somewhere.ui.commonScreenUtils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.somewhere.R
import com.example.somewhere.ui.theme.gray
import com.example.somewhere.ui.theme.white

data class MyIcon(
    val imageVector: ImageVector,
    val size: Dp,
    val color: Color?,  /**if null, set [color] to Material.colors.onSurface, onBackground...*/
    @StringRes val descriptionTextId: Int
)


//MyIcons collection
object MyIcons {

    //
    val delete = MyIcon(Icons.Filled.Delete,            22.dp, gray, R.string.delete)
    val deleteButton = MyIcon(Icons.Filled.Delete,      24.dp, gray, R.string.delete)

    //    val add = MyIcon(Icons.Filled.Add,                  22.dp, null, R.string.new_)
    val deleteImage = MyIcon(Icons.Filled.Close,        16.dp, null, R.string.delete_image)
    val dragHandle = MyIcon(Icons.Filled.DragHandle,    22.dp, gray, R.string.drag_handle)

    //app bar
    val menu = MyIcon(Icons.Filled.Menu,      22.dp, null, R.string.menu)
    val back = MyIcon(Icons.Filled.ArrowBack, 22.dp, null, R.string.back)
    val close = MyIcon(Icons.Filled.Close,    22.dp, null, R.string.close)
    val edit = MyIcon(Icons.Filled.Edit,      24.dp, null, R.string.edit)

//    val more = MyIcon(Icons.Filled.MoreVert,  22.dp, null, R.string.more)
//    val done = MyIcon(Icons.Filled.Done,  22.dp, null, R.string.save)

    //bottom navigation bar
    val myTripsFilled = MyIcon(Icons.Filled.Luggage,            24.dp, null, R.string.my_trips)
    val myTripsOutlined = MyIcon(Icons.Outlined.Luggage,        24.dp, null, R.string.my_trips)

    val moreHorizFilled = MyIcon(Icons.Filled.MoreHoriz,        24.dp, null, R.string.more)
    val moreHorizOutlined = MyIcon(Icons.Outlined.MoreHoriz,    24.dp, null, R.string.more)

//    val profileNotSelected = MyIcon(Icons.Outlined.Person,  24.dp, null, R.string.profile)
//    val profileSelected = MyIcon(Icons.Filled.Person,       24.dp, null, R.string.profile)


    //Floating Action Button
    val add = MyIcon(Icons.Filled.Add,        24.dp, null, R.string.new_)

    //image loading error
    val imageLoadingError = MyIcon(Icons.Rounded.Error,        36.dp, null, R.string.image_loading_error)


    //color dialog
    val selectedColor = MyIcon(Icons.Filled.Done,  22.dp, white, R.string.selected_color)

    //date < >
    val leftArrow = MyIcon(Icons.Filled.KeyboardArrowLeft,   30.dp, null, R.string.previous_date)
    val rightArrow = MyIcon(Icons.Filled.KeyboardArrowRight, 30.dp, null, R.string.next_date)

    //date time
    val date = MyIcon(Icons.Filled.CalendarMonth,   22.dp, gray, R.string.date)
    val time = MyIcon(Icons.Filled.AccessTime,      20.dp, gray, R.string.time)
    val rightArrowTo = MyIcon(Icons.Filled.KeyboardArrowRight, 30.dp, gray, R.string.to)

    //set time
    val switchToTextInput = MyIcon(Icons.Filled.Keyboard,   22.dp, gray, R.string.switch_to_text_input)
    val switchToTouchInput = MyIcon(Icons.Filled.Schedule,   22.dp, gray, R.string.switch_to_touch_input)


    //item expand collapse
    val expand = MyIcon(Icons.Filled.ExpandMore,   22.dp, gray, R.string.expand)
    val collapse = MyIcon(Icons.Filled.ExpandLess, 22.dp, gray, R.string.collapse)

    //information card
    val period = MyIcon(Icons.Filled.SyncAlt,       20.dp, gray, R.string.period)
    val budget = MyIcon(Icons.Filled.Payments,      22.dp, gray, R.string.budget)
    val travelDistance = MyIcon(Icons.Filled.Route, 20.dp, gray, R.string.travel_distance)
    val category = MyIcon(Icons.Filled.Bookmarks,   20.dp, gray, R.string.category)

    //map
    val map = MyIcon(Icons.Filled.Map,                             22.dp, null, R.string.map)
    val focusOnToTarget = MyIcon(Icons.Filled.LocationOn,          24.dp, null, R.string.focus_on_to_target)
    val disabledFocusOnToTarget = MyIcon(Icons.Filled.LocationOn,  24.dp, gray, R.string.disabled_focus_on_to_target)
    val fullscreen = MyIcon(Icons.Filled.Fullscreen,               24.dp, null, R.string.fullscreen_map)
    val myLocation = MyIcon(Icons.Filled.MyLocation,               24.dp, null, R.string.my_location)
    val disabledMyLocation = MyIcon(Icons.Filled.LocationDisabled, 24.dp, gray, R.string.disabled_my_location)
//    val expandControlBox = MyIcon(Icons.Filled.ExpandLess,         24.dp, null, R.string.expand_control_box)
//    val collapseControlBox = MyIcon(Icons.Filled.ExpandMore,       24.dp, null, R.string.collapse_control_box)

    val zoomOutMore = MyIcon(Icons.Filled.Remove,                   26.dp, null, R.string.zoom_out_more)
    val zoomOut = MyIcon(Icons.Filled.Remove,                       18.dp, null, R.string.zoom_out)
    val zoomIn = MyIcon(Icons.Filled.Add,                           18.dp, null, R.string.zoom_in)
    val zoomInMore = MyIcon(Icons.Filled.Add,                       26.dp, null, R.string.zoom_in_more)

    val editLocation = MyIcon(Icons.Filled.Edit,        22.dp, null, R.string.edit_location)
    val deleteLocation = MyIcon(Icons.Filled.Delete,    22.dp, null, R.string.delete_location)
    val cancelEditLocation = MyIcon(Icons.Filled.Close, 22.dp, null, R.string.cancel_edit_location)
    val saveLocation = MyIcon(Icons.Filled.Done,        22.dp, null, R.string.save_location)

    //setting
    val openInNew = MyIcon(Icons.Filled.OpenInNew,        22.dp, gray, R.string.open_in_new)

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
    val contentDescription = stringResource(id = descriptionTextId ?: icon.descriptionTextId)
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