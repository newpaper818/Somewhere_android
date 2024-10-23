package com.newpaper.somewhere.core.designsystem.icon

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material.icons.rounded.East
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FamilyRestroom
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Hiking
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.LocationDisabled
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Luggage
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.ManageAccounts
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.MoreTime
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.TravelExplore
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.newpaper.somewhere.core.designsystem.theme.CustomColor
import com.newpaper.somewhere.core.ui.designsystem.R

data class MyIcon(
    val imageVector: ImageVector,
    val size: Dp = 22.dp,
    val isGray: Boolean = false,
    val color: Color? = null,  /**if null, set [color] to Material.colors.onSurface, onBackground...*/
    @StringRes val descriptionTextId: Int? = null
)





object NavigationBarIcon {
    val tripsFilled = MyIcon(Icons.Rounded.Luggage,       24.dp, false, null, R.string.trips)
    val tripsOutlined = MyIcon(Icons.Outlined.Luggage,    24.dp, false, null, R.string.trips)
    val profileFilled = MyIcon(Icons.Rounded.Person,      24.dp, false, null, R.string.profile)
    val profileOutlined = MyIcon(Icons.Outlined.Person,   24.dp, false, null, R.string.profile)
    val moreFilled = MyIcon(Icons.Rounded.MoreHoriz,      24.dp, false, null, R.string.more)
    val moreOutlined = MyIcon(Icons.Outlined.MoreHoriz,   24.dp, false, null, R.string.more)
}

object TopAppBarIcon {
    val back = MyIcon(Icons.AutoMirrored.Rounded.ArrowBack,     22.dp, false, null, R.string.navigate_up)
    val edit = MyIcon(Icons.Rounded.Edit,                       22.dp, false, null, R.string.edit)
    val close = MyIcon(Icons.Rounded.Close,                     22.dp, false, null, R.string.close)
    val more = MyIcon(Icons.Rounded.MoreVert,                   22.dp, false, null, R.string.more_options)
    val closeImageScreen = MyIcon(Icons.Rounded.Close,          22.dp, false, CustomColor.white, R.string.close)
    val downloadImage = MyIcon(Icons.Rounded.FileDownload,      22.dp, false, CustomColor.white, R.string.download_image)
    val inviteFriend = MyIcon(Icons.Rounded.PersonAdd,          22.dp, false, null, R.string.invite_friend)
}

object IconTextButtonIcon {
    val qrCode = MyIcon(Icons.Rounded.QrCode,                 24.dp, false, null, null)
    val add = MyIcon(Icons.Rounded.Add,                       24.dp, false, null, R.string.add)
    val delete = MyIcon(Icons.Rounded.Delete,                 24.dp, false, null, R.string.delete)
    val leftArrow = MyIcon(Icons.Rounded.KeyboardArrowLeft,   30.dp, false, null, R.string.previous_date)
    val rightArrow = MyIcon(Icons.Rounded.KeyboardArrowRight, 30.dp, false, null, R.string.next_date)
}

object FabIcon {
    val add = MyIcon(Icons.Rounded.Add, 24.dp, false, null, R.string.add)
    val map = MyIcon(Icons.Rounded.Map, 22.dp, false, null, R.string.map)
}

object MapButtonIcon {
    val focusOnToTarget = MyIcon(Icons.Rounded.LocationOn,          24.dp, false, null, R.string.focus_on_to_target)
    val disabledFocusOnToTarget = MyIcon(Icons.Rounded.LocationOn,  24.dp, true, null, R.string.disabled_focus_on_to_target)
    val fullscreen = MyIcon(Icons.Rounded.Fullscreen,               24.dp, false, null, R.string.fullscreen_map)
    val myLocation = MyIcon(Icons.Rounded.MyLocation,               24.dp, false, null, R.string.my_location)
    val disabledMyLocation = MyIcon(Icons.Rounded.LocationDisabled, 24.dp, true, null, R.string.disabled_my_location)

    val zoomOut = MyIcon(Icons.Rounded.Remove,                   26.dp, false, null, R.string.zoom_out)
    val zoomOutLess = MyIcon(Icons.Rounded.Remove,               18.dp, false, null, R.string.zoom_out_less)
    val zoomInLess = MyIcon(Icons.Rounded.Add,                   18.dp, false, null, R.string.zoom_in_less)
    val zoomIn = MyIcon(Icons.Rounded.Add,                       26.dp, false, null, R.string.zoom_in)

    val editLocation = MyIcon(Icons.Rounded.Edit,           24.dp, false, null, R.string.edit_location)
    val deleteLocation = MyIcon(Icons.Rounded.Delete,       24.dp, false, null, R.string.delete_location)

    val prevSpot = MyIcon(Icons.Rounded.KeyboardArrowLeft,  30.dp, false, null, R.string.previous_spot)
    val nextSpot = MyIcon(Icons.Rounded.KeyboardArrowRight, 30.dp, false, null, R.string.next_spot)
    val prevDate = MyIcon(Icons.Rounded.KeyboardArrowLeft,  30.dp, false, null, R.string.previous_date)
    val nextDate = MyIcon(Icons.Rounded.KeyboardArrowRight, 30.dp, false, null, R.string.next_date)

    val openInGoogleMap = MyIcon(Icons.Rounded.Map, 24.dp, false, null, R.string.open_in_google_map)
}

object SelectSwitchIcon {
    val viewOnly = MyIcon(Icons.Rounded.Visibility,     24.dp, false, null, R.string.view_only)
    val allowEdit = MyIcon(Icons.Rounded.Edit,          24.dp, false, null, R.string.allow_edit)
}

object TripWithIcon {
    val solo = MyIcon(Icons.Rounded.Person,             22.dp, false, null, null)
    val partner = MyIcon(Icons.Rounded.Favorite,        22.dp, false, null, null)
    val friends = MyIcon(Icons.Rounded.Group,           22.dp, false, null, null)
    val family = MyIcon(Icons.Rounded.FamilyRestroom,   22.dp, false, null, null)
}

object TripTypeIcon {
    val culturalExperiences = MyIcon(Icons.Rounded.TravelExplore,   22.dp, false, null, null)
    val historicalSites = MyIcon(Icons.Rounded.AccountBalance,  22.dp, false, null, null)
    val exhibitionsMuseums = MyIcon(Icons.Rounded.Image,        22.dp, false, null, null)
    val sportsActivity = MyIcon(Icons.AutoMirrored.Rounded.DirectionsRun,   22.dp, false, null, null)
    val greatFood = MyIcon(Icons.Rounded.LunchDining,           22.dp, false, null, null)
    val shopping = MyIcon(Icons.Rounded.ShoppingBag,            22.dp, false, null, null)
}

val createTripIcons = listOf(
    MyIcon(Icons.Rounded.Luggage,       40.dp, false, null, null),
    MyIcon(Icons.Rounded.Flight,        40.dp, false, null, null),
    MyIcon(Icons.Rounded.Public,        40.dp, false, null, null),
    MyIcon(Icons.Rounded.Hotel,         40.dp, false, null, null),
    MyIcon(Icons.Rounded.BeachAccess,   40.dp, false, null, null),
    MyIcon(Icons.Rounded.Hiking,        40.dp, false, null, null),
    MyIcon(Icons.Rounded.Train,         40.dp, false, null, null),
    MyIcon(Icons.Rounded.Map,           40.dp, false, null, null),
    MyIcon(Icons.Rounded.LunchDining,   40.dp, false, null, null),

    MyIcon(Icons.Rounded.Luggage,       40.dp, false, null, null),
)


object MyIcons {

    //error
    val error = MyIcon(Icons.Rounded.ErrorOutline,              40.dp, false, null, R.string.error)

    //sign in screen
    val signIn = MyIcon(Icons.AutoMirrored.Rounded.Login,       36.dp, true, null, R.string.sign_in)
    val internetUnavailable = MyIcon(Icons.Rounded.CloudOff,    40.dp, true, null, R.string.internet_unavailable)
    val internetUnavailableWhite = MyIcon(Icons.Rounded.CloudOff, 40.dp, false, Color.White, R.string.internet_unavailable)

    //no item
    val noTrips = MyIcon(Icons.Rounded.Luggage,       40.dp, true, null, R.string.no_trips)
    val noPlan = MyIcon(Icons.Rounded.EditNote,       40.dp, true, null, R.string.no_plan)
    val noSpot = MyIcon(Icons.Rounded.LocationOff,    40.dp, true, null, R.string.no_spot)

    //edit profile
    val changeProfileImage = MyIcon(Icons.Rounded.Image,    24.dp, false, null, null)
    val deleteProfileImage = MyIcon(Icons.Rounded.Delete,   24.dp, false, null, null)

    //image card
    val deleteImage = MyIcon(Icons.Rounded.Close,       16.dp, false, null, R.string.delete_image)
    val imageLoadingError = MyIcon(Icons.Rounded.Error, 36.dp, false, null, R.string.image_loading_error)

    //search / text input
    val searchLocation = MyIcon(Icons.Rounded.Search,   24.dp, false, null, R.string.search_location)
    val searchFriend = MyIcon(Icons.Rounded.Search,     24.dp, false, null, R.string.search_friend)
    val clearInputText = MyIcon(Icons.Rounded.Close,    22.dp, false, null, R.string.clear_text)

    //item expand collapse
    val expand = MyIcon(Icons.Rounded.ExpandMore,   22.dp, true, null, R.string.expand)
    val collapse = MyIcon(Icons.Rounded.ExpandLess, 22.dp, true, null, R.string.collapse)

    //
    val delete = MyIcon(Icons.Rounded.Delete,            22.dp, true, null, R.string.delete)
    val deleteSpot = MyIcon(Icons.Rounded.Delete,            22.dp, true, null, R.string.delete_spot)
    val deleteStartTime = MyIcon(Icons.Rounded.Delete,   22.dp, true, null, R.string.delete_start_time)
    val deleteEndTime = MyIcon(Icons.Rounded.Delete,     22.dp, true, null, R.string.delete_end_time)
    val dragHandle = MyIcon(Icons.Rounded.DragHandle,    22.dp, true, null, R.string.drag_handle)
    val clickableItem = MyIcon(Icons.AutoMirrored.Rounded.NavigateNext,    22.dp, true, null, R.string.clickable_item)


    //set color
    val setColor = MyIcon(Icons.Outlined.Palette,   24.dp, false, null, R.string.set_color)
    val selectedColor = MyIcon(Icons.Rounded.Done,  22.dp, false, null, R.string.selected_color)


    //invited friend
    val viewOnly = MyIcon(Icons.Rounded.Visibility,     24.dp, true, null, R.string.view_only)
    val allowEdit = MyIcon(Icons.Rounded.Edit,          24.dp, true, null, R.string.allow_edit)

    //invite friend
    val qrCode = MyIcon(Icons.Rounded.QrCodeScanner,    40.dp, false, null, null)
    val flashOn = MyIcon(Icons.Rounded.FlashOn,         22.dp, false, null, R.string.flash_on)
    val flashOff = MyIcon(Icons.Rounded.FlashOff,       22.dp, false, null, R.string.flash_off)

    //date time
    val date = MyIcon(Icons.Rounded.CalendarMonth,      22.dp, true, null, R.string.date)
    val time = MyIcon(Icons.Rounded.AccessTime,         20.dp, true, null, R.string.time)
    val setTime = MyIcon(Icons.Rounded.MoreTime,        20.dp, true, null, R.string.time)
    val rightArrowTo = MyIcon(Icons.Rounded.East,       22.dp, true, null, R.string.to)
    val rightArrowToSmall = MyIcon(Icons.Rounded.East,  18.dp, true, null, R.string.to)

    //trip creation options dialog
    val manual = MyIcon(Icons.Rounded.Edit,    26.dp, false, null, null)
    val ai = MyIcon(Icons.Rounded.Bolt,         26.dp, false, null, null)

    //set time dialog
    val switchToTextInput = MyIcon(Icons.Outlined.Keyboard,   22.dp, true, null, R.string.switch_to_keyboard_input)
    val switchToTouchInput = MyIcon(Icons.Rounded.Schedule,   22.dp, true, null, R.string.switch_to_touch_input)

    //information card
    val category = MyIcon(Icons.Rounded.Bookmarks,   20.dp, true, null, R.string.category)
    val budget = MyIcon(Icons.Rounded.Payments,      22.dp, true, null, R.string.budget)
    val travelDistance = MyIcon(Icons.Rounded.Route, 20.dp, true, null, R.string.travel_distance)

    //setting
    val openInNew = MyIcon(Icons.Rounded.OpenInNew,     22.dp, true, null, R.string.open_in_new)

    //share trip
    val deleteFriend = MyIcon(Icons.Rounded.Close,      24.dp, true, null, R.string.delete_friend)
    val friends = MyIcon(Icons.Rounded.People,          22.dp, true, null, R.string.number_of_trip_mates)
    val inviteFriend = MyIcon(Icons.Rounded.PersonAdd,  22.dp, false, null, R.string.invite_friend)
    val manager = MyIcon(Icons.Rounded.ManageAccounts,  22.dp, true, null, R.string.manager)
    val leaveTrip = MyIcon(Icons.Rounded.Logout,           24.dp, true, null, R.string.leave_trip)
}